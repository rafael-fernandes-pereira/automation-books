package passo2;


import com.google.gson.Gson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import passo1.ApplyNewPriceRequest;
import passo1.BookDTO;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;

public class ApplyNewValueTest {

    private String url = "http://localhost:8080";

    private BookDTO[] getBooks(ApplyNewPriceRequest request) throws Exception {

        var uriBook = new URI(url + "/book/" + request.getTypeOfApply() + "/" + request.getId());

        var requestOfBooksBeforeDiscount = HttpRequest.newBuilder()
                .uri(uriBook)
                .GET()
                .build();

        var responseOfBooksBeforeDiscount = HttpClient.newBuilder()
                .build()
                .send(requestOfBooksBeforeDiscount, HttpResponse.BodyHandlers.ofString());

        return new Gson().fromJson(responseOfBooksBeforeDiscount.body(), BookDTO[].class);
    }

    private BigDecimal sumOfBooks(ApplyNewPriceRequest request, Boolean isBefore) throws Exception {

        var books = getBooks(request);

        var discountDouble = new BigDecimal(request.getPercentage()).divide(new BigDecimal(100));
        var sumOfBooksBeforeDiscount = new BigDecimal(0);

        for (BookDTO book: books){

            var valueWithDiscount = book.getPrice();

            if (isBefore){
                var valueDiscount = book.getPrice().multiply(discountDouble);
                valueWithDiscount = book.getPrice().subtract(valueDiscount).setScale(2, RoundingMode.HALF_EVEN);
            }

            sumOfBooksBeforeDiscount = sumOfBooksBeforeDiscount.add(valueWithDiscount);
        }

        return sumOfBooksBeforeDiscount;
    }

    private void apllyNewPrice(ApplyNewPriceRequest requestApplyNewPrice) throws URISyntaxException, IOException, InterruptedException {

        var requestApplyNewPriceJson = new Gson().toJson(requestApplyNewPrice);

        var requestOfBooksApplyNewPrice = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/applyNewPrice"))
                .setHeader("Content-type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestApplyNewPriceJson))
                .build();

        var responseOfBookApplyNewPrice = HttpClient.newBuilder()
                .build()
                .send(requestOfBooksApplyNewPrice, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, responseOfBookApplyNewPrice.statusCode());
    }

    @Test
    void testCategory() throws Exception {

        // Given ou Arrange

        var requestApplyNewPrice = new ApplyNewPriceRequest("category",
                3,
                10
        );

        var sumOfBooksBeforeDiscount = sumOfBooks(requestApplyNewPrice, Boolean.TRUE);

        // When or Action

        apllyNewPrice(requestApplyNewPrice);

        var sumOfBooksAfterDiscount = sumOfBooks(requestApplyNewPrice, Boolean.FALSE);

        // Then or Assert

        Assertions.assertEquals(sumOfBooksBeforeDiscount, sumOfBooksAfterDiscount);

    }



}
