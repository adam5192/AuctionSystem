<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="ISO-8859-1">
    <title>Auction</title>
</head>
<body>
    <h1>Available Auctions</h1>

    <!-- Display message if present -->
    <c:if test="${not empty message}">
        <p style="color: green;">${message}</p>
    </c:if>

    <table border="1">
        <thead>
            <tr>
                <th>Item Name</th>
                <th>Current Price</th>
                <th>Auction Type</th>
                <th>End Time</th>
                <th>Action</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="auction" items="${auctions}">
                <tr>
                    <td>${auction.itemName}</td>
                    <td>${auction.currentPrice}</td>
                    <td>${auction.auctionType}</td>
                    <td>${auction.endTimeText}</td>
                    <td>
                        <form action="AuctionListServlet" method="post">
                            <input type="hidden" name="auctionId" value="${auction.id}">
                            <input type="number" name="bidAmount" placeholder="Enter your bid" required>
                            <input type="text" name="bidderId" placeholder="Your ID" required>
                            <button type="submit">Place Bid</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</body>
</html>
