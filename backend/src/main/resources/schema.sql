-- === Clean schema (for development only) ===
DROP SCHEMA public CASCADE;
CREATE SCHEMA public;
SET search_path TO public;

-- === ENUM TYPES ===
CREATE TYPE batch_status AS ENUM ('CHECKING', 'CHECKED');
CREATE TYPE check_result_status AS ENUM ('SUCCESS', 'FAIL');
CREATE TYPE defect_status AS ENUM ('MINOR', 'MAJOR', 'CRITICAL', 'RESOLVED');

-- === TABLE: products ===
CREATE TABLE products (
                          product_id bigserial PRIMARY KEY,
                          product_name varchar(255) NOT NULL,
                          product_type varchar(255) NOT NULL,
                          created_at timestamp DEFAULT now(),
                          updated_at timestamp DEFAULT now()
);

-- === TABLE: batch ===
CREATE TABLE batch (
                       batch_id serial PRIMARY KEY,
                       product_id bigint NOT NULL,
                       quantity integer NOT NULL,
                       status batch_status NOT NULL,
                       notes varchar(255),
                       created_at timestamp DEFAULT now(),
                       updated_at timestamp DEFAULT now()
);

-- === TABLE: checklist_items ===
CREATE TABLE checklist_items (
                                 item_id bigserial PRIMARY KEY,
                                 product_id bigint NOT NULL,
                                 description text NOT NULL,
                                 is_mandatory boolean DEFAULT false,
                                 created_at timestamp DEFAULT now(),
                                 updated_at timestamp DEFAULT now()
);

-- === TABLE: checklist_result ===
CREATE TABLE checklist_result (
                                  checklist_result_id bigserial PRIMARY KEY,
                                  user_id varchar(255) NOT NULL, -- external Keycloak ID
                                  batch_id integer NOT NULL,
                                  status check_result_status NOT NULL DEFAULT 'SUCCESS',
                                  created_at timestamp DEFAULT now(),
                                  updated_at timestamp DEFAULT now()
);

-- === TABLE: checklist_answer ===
CREATE TABLE checklist_answer (
                                  checklist_answer_id bigserial PRIMARY KEY,
                                  result_id bigint,
                                  item_description text NOT NULL,
                                  value boolean,
                                  comment text,
                                  media_url text
);

-- === TABLE: defect_reports ===
CREATE TABLE defect_reports (
                                defect_id bigserial PRIMARY KEY,
                                checklist_result_id bigint NOT NULL,
                                user_id varchar(255) NOT NULL, -- external Keycloak ID
                                description varchar(255) NOT NULL,
                                status defect_status NOT NULL,
                                created_at timestamp DEFAULT now(),
                                updated_at timestamp DEFAULT now(),
                                reviewed_at timestamp,
                                reported_by varchar(255),
                                reviewed_by varchar(255)
);

-- === FOREIGN KEYS ===
ALTER TABLE batch
    ADD CONSTRAINT batch_product_id_fkey FOREIGN KEY (product_id) REFERENCES products (product_id);

ALTER TABLE checklist_items
    ADD CONSTRAINT checklist_items_product_id_fkey FOREIGN KEY (product_id) REFERENCES products (product_id);

ALTER TABLE checklist_result
    ADD CONSTRAINT checklist_result_batch_id_fkey FOREIGN KEY (batch_id) REFERENCES batch (batch_id);

ALTER TABLE checklist_answer
    ADD CONSTRAINT checklist_answer_result_id_fkey FOREIGN KEY (result_id) REFERENCES checklist_result (checklist_result_id);

ALTER TABLE defect_reports
    ADD CONSTRAINT defect_reports_result_id_fkey FOREIGN KEY (checklist_result_id) REFERENCES checklist_result (checklist_result_id);
