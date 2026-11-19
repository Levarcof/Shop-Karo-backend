package ecommerce.app.Ecommerce.repository;

import ecommerce.app.Ecommerce.entity.WebhookEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WebhookEventRepository extends JpaRepository<WebhookEvent , String> {

}
