package org.acme.order.client;

import org.acme.order.client.model.Pastry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest
public class PastryAPIClientTest {

    @Autowired
    private PastryAPIClient client;

    @Test
    public void testListPastries() {
        List<Pastry> pastries = client.listPastries();
        assertEquals(3, pastries.size());
        assertEquals("Baba Rhum", pastries.get(0).name());
    }



    @Test
    public void testGetPastryByName() {
        Pastry pastry = client.getPastryByName("Millefeuille");
        assertNotNull(pastry);
        assertEquals("Millefeuille", pastry.name());
        assertEquals("available", pastry.status());
        assertEquals("L", pastry.size());

        pastry = client.getPastryByName("Eclair Cafe");
        assertNotNull(pastry);
        assertEquals("Eclair Cafe", pastry.name());
        assertEquals("available", pastry.status());
        assertEquals("M", pastry.size());
    }
}
