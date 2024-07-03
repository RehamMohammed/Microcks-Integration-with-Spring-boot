package org.acme.order.service;

import org.acme.order.BaseIntegrationTest;
import org.acme.order.service.model.Order;
import org.acme.order.service.model.OrderInfo;
import org.acme.order.service.model.ProductQuantity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderServiceTests extends BaseIntegrationTest {

   @Autowired
   OrderService service;

   @Test
   void testOrderIsCreated() {
      // Prepare an application Order.
      OrderInfo info = new OrderInfo("123-456-789", List.of(
              new ProductQuantity("Millefeuille", 1),
              new ProductQuantity("Paris-Brest", 1)
      ), 8.4);

      try {
         // Invoke the application to create an order.
         Order createdOrder = service.placeOrder(info);

         // You may check additional stuff on createdOrder...
         assertNotNull(createdOrder);
         assertEquals("123-456-789", createdOrder.getId());
         assertEquals(2, createdOrder.getProductQuantities().size());
         assertEquals(8.4, createdOrder.getTotalPrice());

      } catch (Exception e) {
         fail("No exception should be thrown when creating an order", e);
      }
   }
}
