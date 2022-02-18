package passo1;


import com.google.gson.Gson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ApplyNewPriceTest {

    @Test
    void testCategory() throws Exception {

        // Given ou Arrange

        var discount = 10;
        var typeOfApplyDiscount = "category";
        var id = 3;

        var requestOfBooksBeforeDiscount = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/book/"+typeOfApplyDiscount+"/"+id))
                .GET()
                .build();

        var responseOfBooksBeforeDiscount = HttpClient.newBuilder()
                .build()
                .send(requestOfBooksBeforeDiscount, HttpResponse.BodyHandlers.ofString());

        var books = new Gson().fromJson(responseOfBooksBeforeDiscount.body(), BookDTO[].class);

        var discountDouble = new BigDecimal(discount).divide(new BigDecimal(100));

        var sumOfBooksBeforeDiscount = new BigDecimal(0);

        for (BookDTO book: books){
            var valueDiscount = book.getPrice().multiply(discountDouble);
            var valueWithDiscount = book.getPrice().subtract(valueDiscount).setScale(2, RoundingMode.HALF_EVEN);

            sumOfBooksBeforeDiscount = sumOfBooksBeforeDiscount.add(valueWithDiscount);
        }

        var requestApplyNewPrice = new ApplyNewPriceRequest(typeOfApplyDiscount, id, discount);
        var requestApplyNewPriceJson = new Gson().toJson(requestApplyNewPrice);

        // When or Action

        var requestOfBooksApplyNewPrice = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/applyNewPrice"))
                .setHeader("Content-type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestApplyNewPriceJson))
                .build();

        var responseOfBookApplyNewPrice = HttpClient.newBuilder()
                .build()
                .send(requestOfBooksApplyNewPrice, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, responseOfBookApplyNewPrice.statusCode());

        var requestOfBooksAfterDiscount = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/book/"+typeOfApplyDiscount+"/"+id))
                .GET()
                .build();

        var responseOfBooksAfterDiscount = HttpClient.newBuilder()
                .build()
                .send(requestOfBooksAfterDiscount, HttpResponse.BodyHandlers.ofString());

        var booksAfter = new Gson().fromJson(responseOfBooksAfterDiscount.body(), BookDTO[].class);

        var sumOfBooksAfterDiscount = new BigDecimal(0);

        for (BookDTO book: booksAfter){
            sumOfBooksAfterDiscount = sumOfBooksAfterDiscount.add(book.getPrice());
        }

        // Then or Assert

        Assertions.assertEquals(sumOfBooksBeforeDiscount, sumOfBooksAfterDiscount);

    }

}
