package passo0;

import com.google.gson.Gson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ApplyNewPriceTest {

    @Test
    void testCategory() throws Exception {

        // Given

        var typeOfApply = "category";
        var id = 3;
        var percentage = 10;

        var request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/book/"+typeOfApply+"/"+id))
                .GET()
                .build();

        var response = HttpClient.newBuilder()
                .build()
                .send(request, HttpResponse.BodyHandlers.ofString());

        var gson = new Gson();

        var books = gson.fromJson(response.body(), BookDTO[].class);

        Assertions.assertNotNull(books);

        var book = books[0];

        var discount = new BigDecimal(percentage).divide(new BigDecimal(100));

        var valueDiscount = book.getPrice().multiply(discount);


        var valueWithDiscount = book.getPrice().subtract(valueDiscount).setScale(2, RoundingMode.HALF_EVEN);

        var requestApplyNewPrice = new ApplyDiscountRequest(typeOfApply, id, percentage);
        var requestApplyNewPriceJson = gson.toJson(requestApplyNewPrice);

        var requestOfBooksApplyNewPrice = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/applyNewPrice"))
                .setHeader("Content-type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestApplyNewPriceJson))
                .build();

        var responseOfBookApplyNewPrice = HttpClient.newBuilder()
                .build()
                .send(requestOfBooksApplyNewPrice, HttpResponse.BodyHandlers.ofString());

        var booksDiscount = gson.fromJson(responseOfBookApplyNewPrice.body(), BookDTO[].class);

        var bookDiscount = booksDiscount[0];

        Assertions.assertEquals(valueWithDiscount, bookDiscount.getPrice());

    }

}
