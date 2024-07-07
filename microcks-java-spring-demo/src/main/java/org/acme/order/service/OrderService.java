package org.acme.order.service;

import org.acme.order.client.PastryAPIClient;
import org.acme.order.client.model.Pastry;
import org.acme.order.service.model.Order;
import org.acme.order.service.model.OrderEvent;
import org.acme.order.service.model.OrderInfo;
import org.acme.order.service.model.ProductQuantity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * OrderService is responsible for checking business rules/constraints on Orders.
 * @author laurent
 */
@Service
public class OrderService {

   private static final Logger log = LoggerFactory.getLogger(OrderService.class);

   // This is a dumb implementation of an event sourcing repository. Don't use this in production!
   private final Map<String, List<OrderEvent>> orderEventsRepository = new HashMap<>();

   private final PastryAPIClient pastryRepository;

   OrderService(PastryAPIClient pastryRepository) {
      this.pastryRepository = pastryRepository;
   }

   public Order placeOrder(OrderInfo info) throws UnavailablePastryException, Exception {
      // For all products in order, check the availability calling the Pastry API.
      Map<CompletableFuture<Boolean>, String> availabilityFutures = new HashMap<>();
      for (ProductQuantity productQuantity : info.productQuantities()) {
         availabilityFutures.put(checkPastryAvailability(productQuantity.productName()), productQuantity.productName());
      }

      // Wait for all completable future to finish.
      CompletableFuture.allOf(availabilityFutures.keySet().toArray(new CompletableFuture[0])).join();

//      try {
//         // If one pastry is marked as unavailable, throw a business exception.
//         for (CompletableFuture<Boolean> availabilityFuture : availabilityFutures.keySet()) {
//            if (!availabilityFuture.get()) {
//               String pastryName = availabilityFutures.get(availabilityFuture);
//               throw new UnavailablePastryException(pastryName, "Pastry " + pastryName + " is not available");
//            }
//         }
//      } catch (InterruptedException | ExecutionException e) {
//         throw new Exception("Unexpected exception: " + e.getMessage());
//      }
      // Everything is available! Create a new order.
      Order result = new Order();
      result.setCustomerId(info.customerId());
      result.setProductQuantities(info.productQuantities());
      result.setTotalPrice(info.totalPrice());

      // Persist and publish creation event.
      OrderEvent orderCreated = new OrderEvent(System.currentTimeMillis(), result, "Creation");
      persistOrderEvent(orderCreated);

      return result;
   }
   public Order getOrder(String id) throws OrderNotFoundException {
      List<OrderEvent> orderEvents = orderEventsRepository.get(id);
      if (orderEvents != null) {
         return orderEvents.get(orderEvents.size() - 1).order();
      }
      throw new OrderNotFoundException(id);
   }

   private CompletableFuture<Boolean> checkPastryAvailability(String pastryName) {
      try {
         Pastry pastry = pastryRepository.getPastryByName(pastryName);
         return CompletableFuture.completedFuture("available".equals(pastry.status()));
      } catch (Exception e) {
         log.error("Got exception from Pastry client: {}", e.getMessage());
         return CompletableFuture.completedFuture(false);
      }
   }

   private void persistOrderEvent(OrderEvent event) {
      List<OrderEvent> orderEvents = orderEventsRepository.get(event.order().getId());
      if (orderEvents == null) {
         orderEvents = new ArrayList<>();
      }
      orderEvents.add(event);
      orderEventsRepository.put(event.order().getId(), orderEvents);
   }
}
