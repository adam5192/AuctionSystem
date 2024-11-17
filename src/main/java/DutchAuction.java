import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
/**
 * Represents a dutch auction.
 * A dutch auction is a type of auction where the price decreases until reaching a minimum reserve price.
 * Users can buy at any time before the auction ends.
 * @author adamm
 *
 */
public class DutchAuction extends Auction{
	private int reservePrice;
    private int decrementAmount;
    private int decrementIntervalMins;
    private int startPrice;

    
    // Constructor for loading existing auction
    public DutchAuction(int id, String itemName, int startPrice, int reservePrice, int decrementAmount, int auctionDurationMins, int decrementIntervalMins, LocalDateTime startTime, LocalDateTime endTime) {
        super(id, itemName, startPrice, auctionDurationMins, startTime, endTime);
        this.startPrice = startPrice;
        this.reservePrice = reservePrice;
        this.decrementAmount = decrementAmount;
        this.decrementIntervalMins = decrementIntervalMins;
        this.setAuctionType("Dutch");
        this.setEndTimeText(endTime);
    }
    // Constructor for creating new auction (no id)
    public DutchAuction(String itemName, int startPrice, int reservePrice, int decrementAmount, int auctionDurationMins, int decrementIntervalMins) {
        super(itemName, startPrice, auctionDurationMins);
        this.startPrice = startPrice;
        this.reservePrice = reservePrice;
        this.decrementAmount = decrementAmount;
        this.decrementIntervalMins = decrementIntervalMins;
        this.setAuctionType("Dutch");
        this.setEndTimeText(endTime);
    }
    
    @Override
    public boolean placeBid(int bidAmount, String bidderId) {
        int currentPrice = getCurrentPrice();
        System.out.println(currentPrice);
        
        // Check if the bid amount meets or exceeds the current price
        if (bidAmount >= currentPrice) {
            // Record the bid in the database and end the auction
            String sql = "INSERT INTO bids (auction_id, bidder_id, bid_amount, bid_time) VALUES (?, ?, ?, ?)";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, getId()); // Auction ID
                pstmt.setString(2, bidderId); // Bidder ID
                pstmt.setInt(3, bidAmount); // Bid Amount
                pstmt.setString(4, LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                pstmt.executeUpdate();

                // Update current bid and end the auction
                this.currentBid = bidAmount;
                this.endAuction();
                System.out.println("Bid accepted. Auction ended with bid of $" + bidAmount + " by " + bidderId);

                return true;

            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            System.out.println("Bid is too low. The current price is $" + currentPrice);
            return false;
        }
    }
    
    @Override
    public int getCurrentPrice() {
        int timeElapsedMinutes = (int) java.time.Duration.between(startTime, LocalDateTime.now()).toMinutes();
        int decrements = timeElapsedMinutes / decrementIntervalMins;
        int decreasedPrice = currentBid - (decrements * decrementAmount);
        return Math.max(decreasedPrice, reservePrice); // Ensure price doesn't go below reserve
    }
    
    @Override
    public int getStartPrice() {
    	return startPrice;
    }
    
    @Override
    public int getReservePrice() {
    	return reservePrice;
    }

	@Override
	public int getDecrementAmount() {
		return decrementAmount;
	}

	@Override
	public int getDecrementIntervalMins() {
		return decrementIntervalMins;
	}
}
