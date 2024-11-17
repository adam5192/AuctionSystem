import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Base abstract class for auctions.
 * Contains properties found in both Dutch and Forward auction classes.
 */
public abstract class Auction {
	protected int id;
	protected String itemName;
	protected int currentBid;
	protected String highestBidder;
	protected LocalDateTime startTime;
	protected LocalDateTime endTime;
	private String endTimeText;
	protected boolean isEnded;
	private String auctionType;
	// Constructor for loading existing auction
	public Auction(int id, String itemName, int initialBid, int auctionDurationMins, LocalDateTime startTime, LocalDateTime endTime) {
		this.id = id;
		this.currentBid = initialBid;
		this.isEnded = false;
		this.itemName = itemName;
		this.startTime = startTime;
		this.endTime = endTime;
	}
	
	// Constructor for creating new auction (no id)
	public Auction(String itemName, int initialBid, int auctionDurationMins) {
		this.currentBid = initialBid;
		this.startTime = LocalDateTime.now();
		this.endTime = LocalDateTime.now().plusMinutes(auctionDurationMins);
		this.isEnded = false;
		this.itemName = itemName;
	}
	
	public boolean delete() {
        String sql = "DELETE FROM auctions WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, this.id); // Set the auction ID to delete
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Auction with ID " + id + " deleted successfully.");
                return true;
            } else {
                System.out.println("Auction with ID " + id + " not found.");
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
	
	public boolean isExpired() {
        return LocalDateTime.now().isAfter(endTime);
    }

    public void endAuction() {
        this.isEnded = true;
        this.delete();
    }

    public abstract boolean placeBid(int bidAmount, String bidderId);

    public String getHighestBidder() {
        return highestBidder;
    }

    public int getCurrentBid() {
        return currentBid;
    }

    public boolean isEnded() {
        return isEnded;
    }
    
    public int getId() {
        return id;
    }
    
    public String getItemName() {
    	return itemName;
    }

	public long getRemainingTime() {
		if (isEnded) {
			return 0;
		}
		
		LocalDateTime now = LocalDateTime.now();
		if (now.isAfter(endTime)) {
			return 0;
		}
		
		Duration duration = Duration.between(now, endTime);
		return duration.toMinutes();
	}
	
	public String getTimeRemaining() {
	    LocalDateTime now = LocalDateTime.now();
	    if (now.isAfter(endTime)) {
	        return "Auction ended";
	    }
	    Duration duration = Duration.between(now, endTime);
	    long minutes = duration.toMinutes();
	    long seconds = duration.minusMinutes(minutes).getSeconds();
	    return String.format("%02d minutes %02d seconds remaining", minutes, seconds);
	}

	public abstract int getCurrentPrice();

	public abstract int getStartPrice();

	public abstract int getReservePrice();

	public abstract int getDecrementAmount();

	public abstract int getDecrementIntervalMins();

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public LocalDateTime getEndTime() {
		return endTime;
	}
	
	public String getAuctionType() {
        return auctionType;
    }

    public void setAuctionType(String auctionType) {
        this.auctionType = auctionType;
    }
    
    public String getEndTimeText() {
        return endTimeText;
    }

    public void setEndTimeText(LocalDateTime endTime) {
    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy, h:mm a");
        this.endTimeText = endTime.format(formatter);
    }


}
