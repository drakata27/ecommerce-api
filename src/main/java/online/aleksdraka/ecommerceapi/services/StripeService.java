package online.aleksdraka.ecommerceapi.services;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import online.aleksdraka.ecommerceapi.annotations.VerifyCart;
import online.aleksdraka.ecommerceapi.dtos.ProductDto;
import online.aleksdraka.ecommerceapi.dtos.StripeResponse;
import online.aleksdraka.ecommerceapi.exceptions.EntityNotFoundException;
import online.aleksdraka.ecommerceapi.models.Product;
import online.aleksdraka.ecommerceapi.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class StripeService {
    private final Logger logger = Logger.getLogger(StripeService.class.getName());

    @Value("${stripe.secretKey}")
    private String secretKey;

    @Value("${stripe.successUrl}")
    private String successUrl;

    @Value("${stripe.cancelUrl}")
    private String cancelUrl;

    private final ProductRepository productRepository;

    public StripeService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @VerifyCart
    public StripeResponse checkoutProducts(String username,Long id, List<ProductDto> productsDto) {
        Stripe.apiKey = secretKey;

        List<Product> products = productRepository.findAllById(
                productsDto.stream().map(ProductDto::getId).collect(Collectors.toList())
        );

        List<SessionCreateParams.LineItem> lineItems = productsDto.stream().map(productDto -> {

            Product product = products.stream()
                    .filter(p -> p.getId().equals(productDto.getId()))
                    .findFirst()
                    .orElseThrow(() -> new EntityNotFoundException("Product not found for ID: " + productDto.getId()));

            SessionCreateParams.LineItem.PriceData.ProductData productData = SessionCreateParams.LineItem.PriceData.ProductData.builder()
                    .setName(product.getName())
                    .setDescription(product.getDescription())
                    .build();

            SessionCreateParams.LineItem.PriceData priceData = SessionCreateParams.LineItem.PriceData.builder()
                    .setCurrency("GBP")
                    .setUnitAmount(product.getPrice().longValue() * 100)
                    .setProductData(productData)
                    .build();

            return SessionCreateParams.LineItem.builder()
                    .setQuantity(productDto.getQuantity().longValue())
                    .setPriceData(priceData)
                    .build();
        }).toList();

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .addAllLineItem(lineItems)
                .build();

        Session session;
        try {
            session = Session.create(params);
        } catch (StripeException e) {
            logger.warning(e.getMessage());
            return StripeResponse.builder()
                    .status("FAILURE")
                    .message("Failed to create payment session")
                    .build();
        }

        return StripeResponse.builder()
                .status("SUCCESS")
                .message("Payment session created")
                .sessionId(session.getId())
                .sessionUrl(session.getUrl())
                .build();
    }

}
