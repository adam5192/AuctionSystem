import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
/**
 * Represents a forward auction.
 * A forward auction is a typical auction with a starting price.
 * Highest bidder wins when auction ends.
 * @author adamm
 *
 */
public class ForwardAuction extends Auction {
	// Constructor for loading existing auction
	public ForwardAuction(int id, String itemName, int initialBid, int auctionDurationMins, LocalDateTime startTime, LocalDateTime endTime) {
		super(id, itemName, initialBid, auctionDurationMins, startTime, endTime);
		this.setAuctionType("Forward");
		this.setEndTimeText(endTime);
	}
	// Constructor for creating new auction (no id)
	public ForwardAuction(String itemName, int initialBid, int auctionDurationMins) {
		super(itemName, initialBid, auctionDurationMins);
		this.setAuctionType("Forward");
		this.setEndTimeText(endTime);
	}
	
	@Override
	public boolean placeBid(int bidAmount, String bidderId) {
		if (bidAmount <= currentBid) {
			System.out.println("Bid too low");
			return false;
		}
        
        String sql = "INSERT INTO bids (auction_id, bidder_id, bid_amount, bid_time) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.setString(2, bidderId);
            pstmt.setInt(3, bidAmount);
            pstmt.setString(4, LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            pstmt.executeUpdate();

            // Update the current bid in the auction instance and database
            this.currentBid = bidAmount;
            updateCurrentBid(conn);
            System.out.println("Bid placed successfully!");

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
	}
	
	private void updateCurrentBid(Connection conn) throws SQLException {
        String updateSql = "UPDATE auctions SET start_price = ? WHERE id = ?";
        try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
            updateStmt.setInt(1, currentBid);
            updateStmt.setInt(2, id);
            updateStmt.executeUpdate();
        }
    }
	
	public String getHighestBidder() {
		return highestBidder;
	}

	@Override
	public int getCurrentPrice() {
		return this.getCurrentBid();
	}

	@Override
	public int getStartPrice() {
		return currentBid;
	}

	@Override
	public int getReservePrice() {
		return 0;
	}

	@Override
	public int getDecrementAmount() {
		return 0;
	}

	@Override
	public int getDecrementIntervalMins() {
		return 0;
	}
}
