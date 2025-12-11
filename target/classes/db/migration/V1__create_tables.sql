CREATE TABLE IF NOT EXISTS customer_interactions (
    id BIGSERIAL PRIMARY KEY,
    product_id VARCHAR(255) NOT NULL,
    customer_id VARCHAR(255) NOT NULL,
    customer_rating INT,
    feedback TEXT,
    timestamp TIMESTAMP NOT NULL,
    responses_from_customer_support TEXT,
    interaction_type VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_customer_id ON customer_interactions(customer_id);
CREATE INDEX idx_product_id ON customer_interactions(product_id);
CREATE INDEX idx_timestamp ON customer_interactions(timestamp);
CREATE INDEX idx_interaction_type ON customer_interactions(interaction_type);


CREATE TABLE IF NOT EXISTS upload_jobs (
    id BIGSERIAL PRIMARY KEY,
    filename VARCHAR(255) NOT NULL,
    status VARCHAR(50),
    total_records INT,
    successful_records INT,
    failed_records INT,
    error_message TEXT,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP
);
