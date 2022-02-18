package passo5;


import com.google.gson.Gson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import passo1.ApplyNewPriceRequest;
import passo1.BookDTO;
import passo3.TypeOfApply;
import passo4.TypeOfApplyId;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ThreadLocalRandom;

public class ApplyNewValueTest {

    private String url = "http://localhost:8080";

    private BookDTO[] getBooks(ApplyNewPriceRequest request, TypeOfApply typeOfApply) throws Exception {

        var uriBook = new URI(url + "/book/" + typeOfApply.getPath() + "/" + request.getId());

        var requestOfBooksBeforeDiscount = HttpRequest.newBuilder()
                .uri(uriBook)
                .GET()
                .build();

        var responseOfBooksBeforeDiscount = HttpClient.newBuilder()
                .build()
                .send(requestOfBooksBeforeDiscount, HttpResponse.BodyHandlers.ofString());

        return new Gson().fromJson(responseOfBooksBeforeDiscount.body(), BookDTO[].class);


    }

    private BigDecimal sumOfBooks(ApplyNewPriceRequest request, TypeOfApply typeOfApply, Boolean isBefore) throws Exception {

        var books = getBooks(request, typeOfApply);

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
                .uri(new URI(this.url + "/applyNewPrice"))
                .setHeader("Content-type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestApplyNewPriceJson))
                .build();

        var responseOfBookApplyNewPrice = HttpClient.newBuilder()
                .build()
                .send(requestOfBooksApplyNewPrice, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, responseOfBookApplyNewPrice.statusCode());
    }

    private ThreadLocalRandom random = ThreadLocalRandom.current();

    private Integer randomPercentage(){
        return random.nextInt(1, 25);
    }

    private Integer getRandomId(TypeOfApply typeOfApply) throws Exception {
        var uri = new URI(url + "/" + typeOfApply.getPath() + "/all");

        var request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        var responseOfBooksBeforeDiscount = HttpClient.newBuilder()
                .build()
                .send(request, HttpResponse.BodyHandlers.ofString());

        var ids = new Gson().fromJson(responseOfBooksBeforeDiscount.body(), TypeOfApplyId[].class);

        var position = random.nextInt(ids.length);

        return ids[position].getId();

    }

    @ParameterizedTest
    @EnumSource(TypeOfApply.class)
    void testCategory(TypeOfApply typeOfApply) throws Exception {

        // Given ou Arrange

        var requestApplyNewPrice = new ApplyNewPriceRequest(typeOfApply.getTypeOfApply(),
                this.getRandomId(typeOfApply),
                this.randomPercentage()
        );

        var sumOfBooksBeforeDiscount = sumOfBooks(requestApplyNewPrice, typeOfApply, Boolean.TRUE);

        // When or Action

        apllyNewPrice(requestApplyNewPrice);

        var sumOfBooksAfterDiscount = sumOfBooks(requestApplyNewPrice, typeOfApply, Boolean.FALSE);

        // Then or Assert

        Assertions.assertEquals(sumOfBooksBeforeDiscount, sumOfBooksAfterDiscount);

    }



}
