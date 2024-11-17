import java.util.HashMap;
import java.util.Map;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Handles auctions in the app.
 * Allows for adding, removing and updating auctions.
 *
 */
public class AuctionServer {
    private Map<Integer, Auction> auctions;
    private ScheduledExecutorService scheduler;
    
    public AuctionServer() {
        this.auctions = new HashMap<>();
//        this.scheduler = Executors.newScheduledThreadPool(1);
//        
//        // Check auction expiration every 10 seconds
//        scheduler.scheduleAtFixedRate(this::checkAndEndAuctions, 0, 10, TimeUnit.SECONDS); 
    }
    
    public void saveAuction(Auction auction) {
        String sql = "INSERT INTO auctions (item_name, auction_duration, start_price, reserve_price, decrement_amount, decrement_interval_mins, start_time, end_time, auction_type) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, auction.getItemName());
            pstmt.setLong(2, auction.getRemainingTime());
            pstmt.setInt(3, auction.getStartPrice());
            pstmt.setInt(4, auction.getReservePrice());
            pstmt.setInt(5, auction.getDecrementAmount());
            pstmt.setInt(6, auction.getDecrementIntervalMins());
            pstmt.setString(7, auction.getStartTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            pstmt.setString(8, auction.getEndTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            pstmt.setString(9, auction instanceof DutchAuction ? "Dutch" : "Forward");

            pstmt.executeUpdate();
            System.out.println("Auction saved to database: " + auction.getItemName());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadAuctions() {
        String sql = "SELECT * FROM auctions";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String itemName = rs.getString("item_name");
                int startPrice = rs.getInt("start_price");
                int reservePrice = rs.getInt("reserve_price");
                int decrementAmount = rs.getInt("decrement_amount");
                int decrementIntervalMins = rs.getInt("decrement_interval_mins");
                int auctionDurationMins = rs.getInt("auction_duration");
                LocalDateTime startTime = LocalDateTime.parse(rs.getString("start_time"), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                LocalDateTime endTime = LocalDateTime.parse(rs.getString("end_time"), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                String auctionType = rs.getString("auction_type");

                Auction auction;
                if ("Dutch".equalsIgnoreCase(auctionType)) {
                    auction = new DutchAuction(id, itemName, startPrice, reservePrice, decrementAmount, auctionDurationMins, decrementIntervalMins, startTime, endTime);
                } else {
                    auction = new ForwardAuction(id, itemName, startPrice, auctionDurationMins, startTime, endTime);
                }

                addAuction(auction);
                System.out.println("Loaded auction: " + itemName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void addAuction(Auction auction) {
        auctions.put(auction.id, auction);
    }

    public boolean placeBid(int itemId, int bidAmount, String bidderId) {
        Auction auction = auctions.get(itemId);

        if (auction != null && !auction.isEnded()) {
            boolean bidAccepted = auction.placeBid(bidAmount, bidderId);

            if (auction.isExpired() || auction.isEnded()) {
                auction.endAuction();
                System.out.println("Auction ended for item " + itemId);
            }

            return bidAccepted;
        } else {
            System.out.println("Auction not found or already ended for item " + itemId);
            return false;
        }
    }
    

    public void checkAndEndAuctions() {
        for (Auction auction : auctions.values()) {
            if (auction.isExpired() && !auction.isEnded()) {
                auction.endAuction();
                auction.delete();
                System.out.println("Auction for item " + auction.id + " has ended."); // Close all auction that have expired time-wise
            }
        }
    }

	public Map<Integer, Auction> getAuctions() {
		return auctions;
	}
	
	public void stop() {
		scheduler.shutdown();
	}
}
