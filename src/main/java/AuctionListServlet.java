import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Servlet that displays a list of auctions.
 * Allows user to view auctions and place bid.
 *
 */
@WebServlet("/AuctionListServlet")
public class AuctionListServlet extends HttpServlet {

    private AuctionServer auctionServer;

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            // Initialize database path
            String dbPath = getServletContext().getRealPath("/WEB-INF/auction.db");
            DatabaseConnection.initialize(dbPath);

            // Initialize AuctionServer
            auctionServer = new AuctionServer();
            auctionServer.loadAuctions();
        } catch (Exception e) {
            throw new ServletException("Failed to initialize AuctionServlet", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // Load auctions from the database
            auctionServer.loadAuctions();
            auctionServer.checkAndEndAuctions();
            List<Auction> auctions = new ArrayList<>();
            for (Auction auction: auctionServer.getAuctions().values()) {
            	if(!auction.isEnded() && !auction.isExpired()) {
            		auctions.add(auction);
            	}
            }

            // Pass auctions and optional message to the JSP
            request.setAttribute("auctions", auctions);
            String message = request.getParameter("message");
            if (message != null) {
                request.setAttribute("message", message);
            }

            // Forward to JSP
            request.getRequestDispatcher("auctions.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred while loading auctions.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String auctionIdStr = request.getParameter("auctionId");
        String bidAmountStr = request.getParameter("bidAmount");
        String bidderId = request.getParameter("bidderId");

        String message;

        try {
            int auctionId = Integer.parseInt(auctionIdStr);
            int bidAmount = Integer.parseInt(bidAmountStr);

            boolean bidSuccess = auctionServer.placeBid(auctionId, bidAmount, bidderId);

            if (bidSuccess) {
                message = "Bid placed successfully!";
            } else {
                message = "Failed to place bid. Ensure your bid is higher than the current bid.";
            }
        } catch (NumberFormatException e) {
            message = "Invalid input. Please ensure all fields are filled out correctly.";
        } catch (Exception e) {
            e.printStackTrace();
            message = "An error occurred while placing the bid.";
        }

        // Redirect back to AuctionServlet with a message
        response.sendRedirect("AuctionListServlet?message=" + java.net.URLEncoder.encode(message, "UTF-8"));
    }
}
