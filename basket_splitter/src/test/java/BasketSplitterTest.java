import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ocado.basket.BasketSplitter;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class BasketSplitterTest {

    private List<String> loadBasketFromFile(String basketPath) {
        var basket = new ArrayList<String>();
        var mapper = new ObjectMapper();
        try {
            basket = new ArrayList<>(mapper.readValue(new File(basketPath), new TypeReference<List<String>>() {}));
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        return basket;
    }

    private Map<String, List<String>> loadExpectedOutputFromFile(String pathToExpectedOutputFile) {
        var mapper = new ObjectMapper();
        var expectedOutput = new LinkedHashMap<String, List<String>>();
        try {
            expectedOutput = new LinkedHashMap<>(mapper.readValue(new File(pathToExpectedOutputFile), new TypeReference<Map<String, List<String>>>() {}));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return expectedOutput;
    }

    @Test
    public void givenBasket1_whenSplitBasket_thenExpectResult() {
        String pathToConfigFile = "src/test/testData/config.json", basketPath = "src/test/testData/basket-1.json",
                pathToExpectedOutput = "src/test/testData/basket_1_expected_output.json";
        var basketSplitter = new BasketSplitter(pathToConfigFile);
        var expectedOutput = loadExpectedOutputFromFile(pathToExpectedOutput);
        var basket = loadBasketFromFile(basketPath);

        assertFalse(basket.isEmpty());

        var output = basketSplitter.split(basket);

        assertEquals(output, expectedOutput);
    }

    @Test
    public void givenBasket2_whenSplitBasket_thenExpectResult() {
        String pathToConfigFile = "src/test/testData/config.json", basketPath = "src/test/testData/basket-2.json",
                pathToExpectedOutput = "src/test/testData/basket_2_expected_output.json";
        var basketSplitter = new BasketSplitter(pathToConfigFile);
        var expectedOutput = loadExpectedOutputFromFile(pathToExpectedOutput);
        var basket = loadBasketFromFile(basketPath);

        assertFalse(basket.isEmpty());

        var output = basketSplitter.split(basket);

        assertEquals(output, expectedOutput);
    }

    @Test
    public void givenBasket3_whenSplitBasket_thenExpectResult() {
        String pathToConfigFile = "src/test/testData/config1.json", basketPath = "src/test/testData/basket-3.json",
                pathToExpectedOutput = "src/test/testData/basket_3_expected_output.json";
        var basketSplitter = new BasketSplitter(pathToConfigFile);
        var expectedOutput = loadExpectedOutputFromFile(pathToExpectedOutput);
        var basket = loadBasketFromFile(basketPath);

        assertFalse(basket.isEmpty());

        var output = basketSplitter.split(basket);

        assertEquals(output, expectedOutput);
    }
}
