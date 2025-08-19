-- Insert sample drug order
INSERT INTO public.drug_order (
    id, prescription_group_id, uuid, medication_name, formulation, strength,
    dosage_amount, route_of_admin, frequency, timing_instructions, notes,
    ordered_by, duration, duration_unit, patient_id, visit_id, start_date,
    date_time_prescribed, encounter_date_time, brand, type, dispense_quantity,
    refills_allowed, archived, organisation_unit_id, created_by, date_created,
    modified_by, date_modified, other_details
) OVERRIDING SYSTEM VALUE VALUES (
                                     1, 'group-123-456', '506af83b-591c-4173-8e77-e4b22d263193', 'Paracetamol',
                                     'Tablet', '500mg', '1 tablet', 'Oral', 'TID', 'After meals',
                                     'Take with plenty of water', 'Dr. Smith', '7', 'days', 1, 101,
                                     '2022-05-09', '2022-05-13 09:30:00', '2022-05-13 09:30:00',
                                     'Panadol', 'prescription', 21, 0, 0, 1, 'guest@lamisplus.org',
                                     '2022-05-13 09:30:00.008', 'guest@lamisplus.org', '2022-05-13 09:30:00.008',
                                     NULL
                                 );

-- Insert sample drug dispense
INSERT INTO public.drug_dispense (
    id, drug_order_id, medication_name, uuid, date_time_dispensed, notes,
    brand, dispense_quantity, unit, dispensed_by, start_date, formulation,
    strength, dosage_amount, route_of_admin, site_of_admin, frequency,
    timing_instructions, duration, duration_unit, refills_allowed, archived,
    organisation_unit_id, patient_id, type, created_by, date_created,
    modified_by, date_modified, other_details
) OVERRIDING SYSTEM VALUE VALUES (
                                     1, 1, 'Paracetamol', '706af83b-591c-4173-8e77-e4b22d263194',
                                     '2022-05-13 12:35:00', 'Patient counseled on proper usage', 'Panadol',
                                     21, 'tablet', 'Emeka John', '2022-05-09', 'Tablet', '500mg',
                                     '1 tablet', 'Oral', NULL, 'TID', 'After meals', '7', 'days',
                                     0, 0, 1, 1, 'medication', 'guest@lamisplus.org', '2022-05-13 09:41:40.008',
                                     'guest@lamisplus.org', '2022-05-13 09:41:40.008', NULL
                                 );

-- Set sequence values
SELECT pg_catalog.setval('public.drug_order_id_seq', 1, true);
SELECT pg_catalog.setval('public.drug_dispense_id_seq', 1, true);