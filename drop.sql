begin;

DROP VIEW if exists payments;
DROP VIEW if exists pilot_trips;
DROP VIEW if exists trip_attractions_list;
DROP VIEW if exists client_discounts;
DROP VIEW if exists client_cash_back;
DROP VIEW if exists client_opinions;

DROP TABLE if exists countries CASCADE;
DROP TABLE if exists cities CASCADE;
DROP TABLE if exists travels CASCADE;
DROP TABLE if exists travels_discounts CASCADE;
DROP TABLE if exists attractions CASCADE;
DROP TABLE if exists trips CASCADE;
DROP TABLE if exists pilots CASCADE;
DROP TABLE if exists trip_routes CASCADE;
DROP TABLE if exists clients CASCADE;
DROP TABLE if exists client_statuses CASCADE;
DROP TABLE if exists statuses CASCADE;
DROP TABLE if exists client_trips CASCADE;
DROP TABLE if exists attraction_discounts CASCADE;
DROP TABLE if exists discounts CASCADE;
DROP TABLE if exists trip_dates CASCADE;
DROP TABLE if exists pilot_warrants CASCADE;
DROP TABLE if exists travel_price_changes CASCADE;
DROP TABLE if exists trip_attractions CASCADE;
DROP TABLE if exists opinions CASCADE;

DROP FUNCTION if exists country_id(char);
DROP FUNCTION if exists city_id(char);
DROP FUNCTION if exists find_pilot;
DROP FUNCTION if exists travels_both_directions;
DROP FUNCTION if exists your_a_discount;
DROP FUNCTION if exists your_t_discount;
DROP FUNCTION if exists real_price;
DROP FUNCTION if exists cash_back;
DROP FUNCTION if exists sum_discounts;

DROP SEQUENCE if exists foo;
DROP SEQUENCE if exists seq;

DROP FUNCTION if exists check1;
DROP FUNCTION if exists check2;
DROP FUNCTION if exists check3;
DROP FUNCTION if exists check4;
DROP FUNCTION if exists check5;
DROP FUNCTION if exists check6;
DROP FUNCTION if exists check7;
DROP FUNCTION if exists check8;
DROP FUNCTION if exists check9;
DROP FUNCTION if exists check10;
DROP FUNCTION if exists check11;
DROP FUNCTION if exists check12;
DROP FUNCTION if exists check13;
DROP FUNCTION if exists check14;
DROP FUNCTION if exists check15;
DROP FUNCTION if exists check16;
DROP FUNCTION if exists check17;
DROP FUNCTION if exists check18;
DROP FUNCTION if exists check19;
DROP FUNCTION if exists check20;
DROP FUNCTION if exists check21;
DROP FUNCTION if exists check22;
DROP FUNCTION if exists check23;
DROP FUNCTION if exists check24;
DROP FUNCTION if exists check25;
DROP FUNCTION if exists check26;
DROP FUNCTION if exists check27;
DROP FUNCTION if exists check28;
DROP FUNCTION if exists check29;
DROP FUNCTION if exists check30;
DROP FUNCTION if exists pesel_check;
DROP FUNCTION if exists date_check;
DROP FUNCTION if exists date_check2;
DROP FUNCTION if exists pilot_check;
DROP FUNCTION if exists discount_check1;
DROP FUNCTION if exists discount_check2;
DROP FUNCTION if exists city_of_id;
DROP FUNCTION if exists max_price;

DROP TYPE if exists attractions_type;
DROP TYPE if exists travel_type;
commit;