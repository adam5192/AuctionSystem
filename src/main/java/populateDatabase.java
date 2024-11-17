/**
 * Run this file to populate the database with some example auctions
 */
public class populateDatabase {
	public static void main(String[] args) {
		// Initialize database path
        String dbPath = "src/main/webapp/WEB-INF/auction.db";
        DatabaseConnection.initialize(dbPath);
		AuctionServer server = new AuctionServer();
        ForwardAuction newForwardAuction = new ForwardAuction("Phone", 500, 2);
        DutchAuction dutchAuction = new DutchAuction("Computer", 1000, 500, 10, 20, 1);
		server.saveAuction(newForwardAuction);
		server.saveAuction(dutchAuction);
	}
}
