begin;

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
DROP TABLE if exists attraction_categories CASCADE;
DROP TABLE if exists trip_attractions CASCADE;
DROP TABLE if exists trip_dates CASCADE;
DROP TABLE if exists pilot_warrants CASCADE;
DROP TABLE if exists travel_price_changes CASCADE;
DROP TABLE if exists opinions CASCADE;

--tables and enums
create type attractions_type as enum ('monument', 'beach', 'modern architecture','museum', 'opera', 'market square', 'palace', 'castle', 'nature', 'other');

create type travel_type as enum('plane', 'train', 'bus', 'ship', 'other');

create or replace function your_a_discount(client int, att int) returns numeric as
$$
declare
	max numeric=0;
	x record;
begin
	for x in select * from attraction_discounts a join discounts d on d.id=a.discount_id where attraction_id=$2
	and status_id in (select status_id from clients c join client_statuses cs on c.id=cs.client_id where client_id=$1) loop
		if x.value>max then max=x.value; end if;
	end loop;
	return max;
end;
$$ language plpgsql;

create or replace function your_t_discount(client int, t int) returns numeric as
$$
declare
	max numeric=0;
	x record;
begin
	for x in select * from travels_discounts t join discounts d on d.id=t.discount_id where travel_id=$2
	and status_id in (select status_id from clients c join client_statuses cs on c.id=cs.client_id where client_id=$1) loop
		if x.value>max then max=x.value; end if;
	end loop;
	return max;
end;
$$ language plpgsql;

create or replace function real_price (cliect int, trip_date int) returns numeric as
$$
declare
	result numeric=0;
	x record;
	t int;
	sth numeric;
begin
	t=(select trip_id from trip_dates where id=$2);
	result=result+(select base_price from trips where id=t);
	for x in select * from trip_attractions ta join attractions a on a.id=ta.attraction_id where ta.trip_id=t and ta.mandatory=true loop
		result=result+(x.price-your_a_discount($1, x.attraction_id));
	end loop;
	for x in select * from trip_routes tr join travels tl on tr.travel_id=tl.id left outer join travel_price_changes tpc on tl.id=tpc.travel_id where trip_id=t loop
		sth=x.base_price;
		if (select starting_date from trip_dates where id=$2)+x.on_day*interval '1 day' between x.valid_since and x.valid_to then sth=sth-x.price_change*sth/100; end if;
			result=result+(sth-your_t_discount($1, x.travel_id));
	end loop;
	return result;
end;
$$ language plpgsql;

create or replace function sum_discounts (cliect int, trip_date int) returns numeric as
$$
declare
	result numeric=0;
	x record;
	t int;
	sth numeric;
begin
	t=(select trip_id from trip_dates where id=$2);
	for x in select * from trip_attractions ta join attractions a on a.id=ta.attraction_id where ta.trip_id=t and ta.mandatory=true loop
		result=result+your_a_discount($1, x.attraction_id);
	end loop;
	for x in select * from trip_routes tr join travels tl on tr.travel_id=tl.id left outer join travel_price_changes tpc on tl.id=tpc.travel_id where trip_id=t loop
		sth=x.base_price;
		if (select starting_date from trip_dates where id=$2)+x.on_day*interval '1 day' between x.valid_since and x.valid_to then sth=sth-x.price_change*sth/100; end if;
			result=result+ your_t_discount($1, x.travel_id);
	end loop;
	return result;
end;
$$ language plpgsql;

create table statuses(
	id serial primary key,
	name char(15) not null
);

create table discounts(
	id integer primary key,
	status_id integer not null references statuses,
	value numeric(6,2) check(value>0)
);

create table countries (
	id serial primary key,
	"name" varchar(50) not null
);

create table cities (
	id serial primary key,
	country_id integer not null references countries(id),
	"name" varchar(100) not null
);

create table travels (
	id serial primary key,
	from_city integer references cities(id),
	to_city integer references cities(id),
	duration numeric(4, 2),
	base_price numeric(6, 2),
	kind travel_type,
	check (from_city != to_city)
);

create table travel_price_changes (
	travel_id integer not null references travels,
	price_change integer not null, --procentowa
	valid_since date,
	valid_to date,
	check (-100<=price_change and price_change<=100),
	check (valid_since<valid_to)
);

create table travels_discounts (
	travel_id integer not null references travels(id),
	discount_id integer not null references discounts
);

create table attractions (
	id serial primary key,
	"name" varchar(100) not null,
	price numeric (6,2) not null,
	city_id integer not null references cities(id),
	type attractions_type not null,
	check(price>=0)
);

create table pilots(
	id serial primary key,
	name varchar(20) not null,
	surname varchar(50) not null
);

create table trips (
	id serial primary key,
	name varchar(100) not null,
	base_price numeric(6,2) check(base_price>0),
	min_people integer check(max_people>0),
	max_people integer check(min_people>0),
	length integer not null,
	check(length>0 and length<=30),
	check(min_people<=max_people)
);

create table pilot_warrants (
	pilot_id integer not null references pilots,
	trip_id integer not null references trips
);

create table trip_dates (
	id serial primary key,
	trip_id integer not null references trips,
	pilot_id integer references pilots,
	starting_date date,
	due_payment_date date,
	check(due_payment_date<starting_date - 2*interval '1 week')
);

create table trip_routes(
	id integer primary key, --seq
	trip_id integer not null references trips,
	travel_id integer not null references travels,
	on_day integer not null
);

create table trip_attractions (
	trip_id integer not null references trips,
	attraction_id integer not null references attractions,
	mandatory boolean not null
);

create table clients(
	id serial primary key,
	name varchar(20) not null,
	surname varchar(20) not null,
	PESEL char(11) not null,
	account_number char(26),
	unique (pesel,account_number)
);

create table client_statuses(
	client_id integer not null references clients,
	status_id integer not null references statuses,
	unique(client_id, status_id)
);

create table attraction_discounts(
	attraction_id integer not null references attractions,
	discount_id integer not null references discounts
);

create table client_trips(
	id serial primary key,
	trip_id integer not null references trip_dates,
	client_id integer not null references clients,
	paid_amount numeric(6,2) check(paid_amount>0),
	check(paid_amount <=real_price(client_id, trip_id))
);

create table opinions (
	client_id integer references clients,
	opinion varchar(1000)
);

create index client_trips_idx on client_trips(client_id);

commit;
--functions
begin;

create or replace function country_id(country char) returns integer as
$$
begin
	return (select id from countries where name=country);
end;
$$ language plpgsql;
create or replace function city_id(city char) returns integer as
$$
begin
	return (select id from cities  where name=city);
end;
$$ language plpgsql;

commit;
--triggers
begin;

create or replace function discount_check1() returns trigger as $discount_check1$
begin
	if (select price from attractions where id=new.attraction_id)<(select value from discounts where id=new.discount_id) then
		raise exception 'discount bigger than price - aborting operation';
	end if;
	return new;
end;
$discount_check1$ language plpgsql;
create trigger discount_check1 before insert or update on attraction_discounts for each row execute procedure discount_check1();

create or replace function discount_check2() returns trigger as $discount_check2$
begin
	if (select base_price from travels where id=new.travel_id)<(select value from discounts where id=new.discount_id) then
		raise exception 'discount bigger than price - aborting operation';
	end if;
	return new;
end;
$discount_check2$ language plpgsql;
create trigger discount_check2 before insert or update on travels_discounts for each row execute procedure discount_check2();

CREATE OR REPLACE FUNCTION check1() RETURNS trigger AS $check1$
declare
p record;
r record;
i integer=-1;
BEGIN
for p in select * from cities where country_id=Old.id loop
for r in select * from travels where p.id=from_city or p.id=to_city loop
raise exception 'City from this country is in travels';
end loop;
end loop;
for p in select * from cities where country_id=Old.id loop
i=p.id;
delete from cities where id=i;
end loop;
return old;
END;
$check1$ LANGUAGE plpgsql;
CREATE TRIGGER check1 BEFORE delete ON countries
FOR EACH ROW EXECUTE PROCEDURE check1();

CREATE OR REPLACE FUNCTION check2() RETURNS trigger AS $check2$
declare
r record;
BEGIN
for r in select * from travels where old.id=from_city or old.id=to_city loop
raise exception 'This city is in travels';
end loop;
delete from attractions where city_id=Old.id;
return old;
END;
$check2$ LANGUAGE plpgsql;
CREATE TRIGGER check2 BEFORE delete ON cities
FOR EACH ROW EXECUTE PROCEDURE check2();

CREATE OR REPLACE FUNCTION check3() RETURNS trigger AS $check3$
BEGIN
delete from attraction_discounts where attraction_id=Old.id;
delete from trip_attractions where attraction_id=Old.id;
return old;
END;
$check3$ LANGUAGE plpgsql;
CREATE TRIGGER check3 BEFORE delete ON attractions
FOR EACH ROW EXECUTE PROCEDURE check3();

CREATE OR REPLACE FUNCTION check4() RETURNS trigger AS $check4$
BEGIN
delete from client_statuses where client_id=Old.id;
delete from client_trips where client_id=Old.id;
update opinions set client_id=null where client_id=Old.id;
return old;
END;
$check4$ LANGUAGE plpgsql;
CREATE TRIGGER check4 BEFORE delete ON clients
FOR EACH ROW EXECUTE PROCEDURE check4();


CREATE OR REPLACE FUNCTION check5() RETURNS trigger AS $check5$
BEGIN
delete from travels_discounts where discount_id=Old.id;
delete from attraction_discounts where discount_id=Old.id;
return old;
END;
$check5$ LANGUAGE plpgsql;
CREATE TRIGGER check5 BEFORE delete ON discounts
FOR EACH ROW EXECUTE PROCEDURE check5();

CREATE OR REPLACE FUNCTION check6() RETURNS trigger AS $check6$
BEGIN
delete from trip_dates where trip_id=Old.id;
delete from trip_routes where trip_id=Old.id;
delete from pilot_warrants where trip_id=Old.id;
delete from trip_attractions where trip_id=Old.id;
return old;
END;
$check6$ LANGUAGE plpgsql;
CREATE TRIGGER check6 BEFORE delete ON trips
FOR EACH ROW EXECUTE PROCEDURE check6();


CREATE OR REPLACE FUNCTION pesel_check() RETURNS trigger AS $pesel_check$
declare
result numeric=0;
BEGIN
  IF length(NEW.pesel)!=11 then
    RAISE EXCEPTION 'Niepoprawny PESEL';
  END IF;
result=result + substring(New.pesel from 1 for 1)::integer;
result=result + substring(New.pesel from 2 for 1)::integer*3;
result=result + substring(New.pesel from 3 for 1)::integer*7;
result=result + substring(New.pesel from 4 for 1)::integer*9;
result=result + substring(New.pesel from 5 for 1)::integer;
result=result + substring(New.pesel from 6 for 1)::integer*3;
result=result + substring(New.pesel from 7 for 1)::integer*7;
result=result + substring(New.pesel from 8 for 1)::integer*9;
result=result + substring(New.pesel from 9 for 1)::integer;
result=result + substring(New.pesel from 10 for 1)::integer*3;
IF (10-result%10)%10!=substring(New.pesel from 11 for 1)::integer then
    RAISE EXCEPTION 'Niepoprawny PESEL';
  END IF;
  RETURN NEW;
END;
$pesel_check$ LANGUAGE plpgsql;
 
CREATE TRIGGER pesel_check BEFORE INSERT OR UPDATE ON clients
FOR EACH ROW EXECUTE PROCEDURE pesel_check();

create or replace function find_pilot(x date,y date,z integer) returns integer as
$$
declare
r record;
p record;
i integer=-1;
t boolean=false;
begin
select x as starting_date,y as finish_date, z as trip_id into p;
for r in select k.id as id,starting_date, starting_date + tt.length*interval '1 day' as finish_date from (select pilot_id as id from pilot_warrants
where trip_id=p.trip_id) k left outer join trip_dates t on t.pilot_id=k.id left outer join trips tt on tt.id=t.trip_id 
left outer join (select pp.id as id,sum(case when td.id is null then 0 else 1 end) as c from pilots pp left outer join trip_dates td on pp.id=pilot_id group by pp.id) l
on k.id=l.id order by l.c,k.id loop 
if i=r.id  then
if r.starting_date is null then
return i;
end if;
if r.starting_date>=p.starting_date then
if p.finish_date>r.starting_date then
t=true;
end if;
end if;
if p.starting_date>=r.starting_date then
if r.finish_date>p.starting_date then
t=true;
end if;
end if;
else
if t=false and i<>-1 then
return i;
end if;
i=r.id;
t=false;
if r.starting_date is null then
return i;
end if;
if r.starting_date>=p.starting_date then
if p.finish_date>r.starting_date then
t=true;
end if;
end if;
if p.starting_date>=r.starting_date then
if r.finish_date>p.starting_date then
t=true;
end if;
end if;
end if;
end loop;
if t=false and i<>-1 then
return i;
end if;
return -1;
end;
$$
language plpgsql;

CREATE OR REPLACE FUNCTION check7() RETURNS trigger AS $check7$
declare
p record;
i integer;
d date;
BEGIN
for p in select * from trip_dates where pilot_id=Old.id loop
select p.starting_date + length*interval '1 day' into d from trips t where t.id=p.trip_id; 
select find_pilot(p.starting_date,d,p.trip_id) into i;
if i=-1 then
raise exception 'There is no available pilot for % trip now',p.id::text ;
else
update trip_dates set pilot_id=i where id=p.id;
end if;
end loop;
delete from pilot_warrants where pilot_id=Old.id;
return old;
END;
$check7$ LANGUAGE plpgsql;
CREATE TRIGGER check7 BEFORE delete ON pilots
FOR EACH ROW EXECUTE PROCEDURE check7();

create or replace function pilot_check(x integer,y integer,zz date, v date) returns integer as
$$
declare
r record;
p record;
z record;
i integer=-1;
begin
select zz as starting_date,v as finish_date into p;
for z in select trip_id from pilot_warrants where pilot_id=x loop
if z.trip_id=y then
i=1;
end if;
end loop;
if i=-1 then
return -1;
end if;
for r in select starting_date, starting_date + tt.length*interval '1 day' as finish_date 
from trip_dates join trips tt on trip_id=tt.id where x=pilot_id loop 
if r.starting_date>=p.starting_date then
if p.finish_date>r.starting_date then
return -1;
end if;
end if;
if p.starting_date>=r.starting_date then
if r.finish_date>p.starting_date then
return -1;
end if;
end if;
end loop;
return 1;
end;
$$
language plpgsql;


CREATE OR REPLACE FUNCTION check8() RETURNS trigger AS $check8$
declare
i integer=-1;
d date;
BEGIN
select New.starting_date + length*interval '1 day' into d from trips t where t.id=New.trip_id; 
if New.pilot_id is distinct from null then
select pilot_check(New.pilot_id,New.trip_id,New.starting_date,d) into i;
end if;
if i=-1 then
raise exception 'Pilot % cannot be in this trip',New.pilot_id::text ;
end if;
return New;
END;
$check8$ LANGUAGE plpgsql;
CREATE TRIGGER check8 BEFORE update on trip_dates
FOR EACH ROW EXECUTE PROCEDURE check8();


CREATE OR REPLACE FUNCTION check9() RETURNS trigger AS $check9$
declare
p record;
i integer;
d date;
BEGIN
for p in select * from trip_dates where pilot_id=Old.pilot_id and trip_id=Old.trip_id loop
select p.starting_date + length*interval '1 day' into d from trips t where t.id=p.trip_id; 
select find_pilot(p.starting_date,d,p.trip_id) into i;
if i=-1 then
raise exception 'There is no available pilot for % trip now',p.id::text ;
else
update trip_dates set pilot_id=i where id=p.id;
end if;
end loop;
return old;
END;
$check9$ LANGUAGE plpgsql;
CREATE TRIGGER check9 BEFORE delete ON pilot_warrants
FOR EACH ROW EXECUTE PROCEDURE check9();

CREATE OR REPLACE FUNCTION check10() RETURNS trigger AS $check10$
declare
BEGIN
raise exception 'What are you doing,bro??';
END;
$check10$ LANGUAGE plpgsql;
CREATE TRIGGER check10 BEFORE update ON pilot_warrants
FOR EACH ROW EXECUTE PROCEDURE check10();

CREATE OR REPLACE FUNCTION check11() RETURNS trigger AS $check11$
declare
BEGIN
if old.country_id<>new.country_id then
raise exception 'Update is not allowed! You can delete and insert instead!';
end if;
return new;
END;
$check11$ LANGUAGE plpgsql;
CREATE TRIGGER check11 BEFORE update ON cities
FOR EACH ROW EXECUTE PROCEDURE check11();

CREATE OR REPLACE FUNCTION check12() RETURNS trigger AS $check12$
declare
BEGIN
if Old.city_id!=New.city_id then
raise exception 'Update is not allowed! You can delete and insert instead!';
end if;
return new;
END;
$check12$ LANGUAGE plpgsql;
CREATE TRIGGER check12 BEFORE update ON attractions
FOR EACH ROW EXECUTE PROCEDURE check12();

CREATE OR REPLACE FUNCTION check13() RETURNS trigger AS $check13$
declare
BEGIN
raise exception 'Update is not allowed! You can delete and insert instead!';
END;
$check13$ LANGUAGE plpgsql;
CREATE TRIGGER check13 BEFORE update ON attraction_discounts
FOR EACH ROW EXECUTE PROCEDURE check13();


CREATE OR REPLACE FUNCTION check14() RETURNS trigger AS $check14$
declare
BEGIN
if Old.trip_id!=New.trip_id or Old.client_id!=New.client_id then
raise exception 'Update is not allowed! You can delete and insert instead!';
end if;
return new;
END;
$check14$ LANGUAGE plpgsql;
CREATE TRIGGER check14 BEFORE update ON client_trips
FOR EACH ROW EXECUTE PROCEDURE check14();

CREATE OR REPLACE FUNCTION check15() RETURNS trigger AS $check15$
declare
BEGIN
raise exception 'Update is not allowed! You can delete and insert instead!';
END;
$check15$ LANGUAGE plpgsql;
CREATE TRIGGER check15 BEFORE update ON client_statuses
FOR EACH ROW EXECUTE PROCEDURE check15();


CREATE OR REPLACE FUNCTION check16() RETURNS trigger AS $check16$
declare
BEGIN
if Old.trip_id!=New.trip_id or Old.attraction_id!=New.attraction_id then
raise exception 'Update is not allowed! You can delete and insert instead!';
end if;
return new;
END;
$check16$ LANGUAGE plpgsql;
CREATE TRIGGER check16 BEFORE update ON trip_attractions
FOR EACH ROW EXECUTE PROCEDURE check16();

CREATE OR REPLACE FUNCTION check17() RETURNS trigger AS $check17$
declare
BEGIN
if Old.status_id!=New.status_id then
raise exception 'Update is not allowed! You can delete and insert instead!';
end if;
END;
$check17$ LANGUAGE plpgsql;
CREATE TRIGGER check17 BEFORE update ON discounts
FOR EACH ROW EXECUTE PROCEDURE check17();

CREATE OR REPLACE FUNCTION check18() RETURNS trigger AS $check18$
declare
BEGIN
raise exception 'Update is not allowed! You can delete and insert instead!';
END;
$check18$ LANGUAGE plpgsql;
CREATE TRIGGER check18 BEFORE update ON travels_discounts
FOR EACH ROW EXECUTE PROCEDURE check18();

CREATE OR REPLACE FUNCTION check19() RETURNS trigger AS $check19$
declare
BEGIN
if Old.from_city!=New.from_city or Old.to_city!=New.to_city then
raise exception 'Update is not allowed! You can delete and insert instead!';
end if;
return new;
END;
$check19$ LANGUAGE plpgsql;
CREATE TRIGGER check19 BEFORE update ON travels
FOR EACH ROW EXECUTE PROCEDURE check19();

CREATE OR REPLACE FUNCTION check20() RETURNS trigger AS $check20$
declare
BEGIN
if Old.travel_id!=New.travel_id then
raise exception 'Update is not allowed! You can delete and insert instead!';
end if;
return new;
END;
$check20$ LANGUAGE plpgsql;
CREATE TRIGGER check20 BEFORE update ON travel_price_changes
FOR EACH ROW EXECUTE PROCEDURE check20();

CREATE OR REPLACE FUNCTION check21() RETURNS trigger AS $check21$
declare
p record;
r record;
i integer=1;
BEGIN
for p in select * from trip_routes where travel_id=Old.id loop
i=-1;
exit;
end loop;
if i=-1 then
raise exception 'ERROR, this travel is in trip_routes';
end if;
delete from travel_price_changes where travel_id=old.id;
delete from travels_discounts where travel_id=old.id;
return old;
END;
$check21$ LANGUAGE plpgsql;
CREATE TRIGGER check21 BEFORE delete ON travels
FOR EACH ROW EXECUTE PROCEDURE check21();


create or replace function date_check(x integer,y integer, z integer) returns integer as
$$
declare
r record;
i integer=-1;
t integer;
f integer;
begin
select length into i from trips where id=y;
if i=-1 then
return -1;
end if;
if i<x then
return -2;
end if;
for r in select on_day,travel_id from trip_routes where y=trip_id order by id desc loop 
i=r.on_day;
if i>x then
return -2;
end if;
select to_city into t from travels where id=r.travel_id;
select from_city into f from travels where id=z;
if t<>f then
return -3;
end if;
exit;
end loop;
return 1;
end;
$$
language plpgsql;

CREATE SEQUENCE foo START 1 INCREMENT BY 1;
CREATE OR REPLACE FUNCTION check22() RETURNS trigger AS $check22$
declare
i integer;
BEGIN
if New.trip_id is null or New.travel_id is null or New.on_day is null then
raise exception 'Error';
end if;
select date_check(New.on_day,New.trip_id, New.travel_id) into i;
if i=-1 then
raise exception 'Error';
end if;
if i=-2 then
raise exception 'Wrong on_day number';
end if;
if i=-3 then
raise exception 'Wrong travel';
else
select nextval('foo') into New.id;
return New;
end if;
END;
$check22$ LANGUAGE plpgsql;
CREATE TRIGGER check22 BEFORE insert ON trip_routes
FOR EACH ROW EXECUTE PROCEDURE check22();

CREATE OR REPLACE FUNCTION check23() RETURNS trigger AS $check23$
declare
p record;
BEGIN
for p in select * from trip_routes where trip_id=old.trip_id order by on_day desc loop
if old.on_day=p.on_day then
delete from trip_attractions where attraction_id=any(select tt.id from travels t
join attractions tt on tt.city_id=t.to_city where t.id=old.trip_id);
return old;
end if;
exit;
end loop;
raise exception 'Error';
END;
$check23$ LANGUAGE plpgsql;
CREATE TRIGGER check23 BEFORE delete ON trip_routes
FOR EACH ROW EXECUTE PROCEDURE check23();

CREATE SEQUENCE seq START 1 INCREMENT BY 1;
 
CREATE OR REPLACE FUNCTION check24() RETURNS trigger AS $check24$
declare
i integer;
BEGIN
if old.id is null then
select nextval('seq') into new.id;
else
new.id=old.id;
end if;
return New;
end;
$check24$ LANGUAGE plpgsql;
CREATE TRIGGER check24 BEFORE insert or update ON travels
FOR EACH ROW EXECUTE PROCEDURE check24();


create or replace function date_check2(x integer,y integer, z integer,v integer) returns integer as
$$
declare
r record;
p record;
i integer=-1;
t integer;
f integer;
begin
select length into i from trips where id=y;
if i=-1 then
return -1;
end if;
if i<x then
return -2;
end if;
i=0;
for r in select id,on_day,travel_id from trip_routes where y=trip_id order by id loop 
if r.id<v then
select r.on_day as on_day,r.travel_id as travel_id into p;
end if;
if r.id=v and i<>0 then
i=p.on_day;
if i>x then
return -2;
end if;
select to_city into t from travels where id=p.travel_id;
select from_city into f from travels where id=z;
if t<>f then
return -3;
end if;
end if;
if r.id>v then
i=r.on_day;
if x>i then
return -2;
end if;
select from_city into t from travels where id=r.travel_id;
select to_city into f from travels where id=z;
if t<>f then
return -3;
end if;
exit;
end if;
i=i+1;
end loop;
return 1;
end;
$$
language plpgsql;


CREATE OR REPLACE FUNCTION check25() RETURNS trigger AS $check25$
declare
i integer;
BEGIN
if New.trip_id is null or New.travel_id is null or New.on_day is null then
raise exception 'Error';
end if;
if New.trip_id<>Old.trip_id then
raise exception 'Error';
end if;
select date_check2(New.on_day,New.trip_id, New.travel_id,old.id) into i;
if i=-1 then
raise exception 'Error';
end if;
if i=-2 then
raise exception 'Wrong on_date number';
end if;
if i=-3 then
raise exception 'Wrong travel';
else
New.id=Old.id;
return New;
end if;
END;
$check25$ LANGUAGE plpgsql;
CREATE TRIGGER check25 BEFORE update ON trip_routes
FOR EACH ROW EXECUTE PROCEDURE check25();

create or replace function travels_both_directions(integer,integer,numeric,numeric,travel_type) returns void as
$$
declare
r record;
begin
insert into travels(from_city,to_city,duration,base_price,kind) values ($1,$2,$3,$4,$5);
insert into travels(from_city,to_city,duration,base_price,kind) values ($2,$1,$3,$4,$5);
end;
$$
language plpgsql;

CREATE OR REPLACE FUNCTION check26() RETURNS trigger AS $check26$
declare
i integer;
d date;
BEGIN
select New.starting_date + length*interval '1 day' into d from trips t where t.id=New.trip_id; 
New.pilot_id=find_pilot(New.starting_date,d,New.trip_id);
if New.pilot_id=-1 then
raise exception 'There is no available pilot for % trip now',p.id::text ;
else
return new;
end if;
END;
$check26$ LANGUAGE plpgsql;
CREATE TRIGGER check26 BEFORE insert ON trip_dates
FOR EACH ROW EXECUTE PROCEDURE check26();

CREATE OR REPLACE FUNCTION check27() RETURNS trigger AS $check27$
declare
i integer;
j integer;
BEGIN
select max_people into i from trips t join trip_dates tt on t.id=tt.trip_id 
join client_trips c on c.trip_id=tt.id where c.trip_id=New.trip_id;
select count(client_id) into j from client_trips where trip_id=New.trip_id group by trip_id;
if j is null or j+1<=i then
return new;
end if;
raise exception 'You cannot sign up this trip, because this trip has already had max_people'; 
END;
$check27$ LANGUAGE plpgsql;
CREATE TRIGGER check27 BEFORE insert ON client_trips
FOR EACH ROW EXECUTE PROCEDURE check27();

CREATE OR REPLACE FUNCTION check28() RETURNS trigger AS $check28$
declare
p record;
i integer;
BEGIN
select city_id into i from attractions where id=New.attraction_id;
for p in select from_city, to_city from travels t join trip_routes tt on tt.travel_id=t.id
where tt.trip_id=New.trip_id loop
if i=p.from_city or i=p.to_city then
return New;
end if;
end loop;
raise exception 'This attraction cannot be in this trip'; 
END;
$check28$ LANGUAGE plpgsql;
CREATE TRIGGER check28 BEFORE insert or update ON trip_attractions
FOR EACH ROW EXECUTE PROCEDURE check28();

CREATE OR REPLACE FUNCTION check29() RETURNS trigger AS $check29$
BEGIN
delete from client_trips where trip_id=Old.id;
return old;
END;
$check29$ LANGUAGE plpgsql;
CREATE TRIGGER check29 BEFORE delete ON trip_dates
FOR EACH ROW EXECUTE PROCEDURE check29();

CREATE OR REPLACE FUNCTION check30() RETURNS trigger AS $check30$
BEGIN
if new.client_id is distinct from null and (old.client_id is null or new.client_id <>old.client_id ) then
raise exception 'Update is not allowed! You can delete and insert instead!';
end if;
return new;
END;
$check30$ LANGUAGE plpgsql;
CREATE TRIGGER check30 BEFORE update ON opinions
FOR EACH ROW EXECUTE PROCEDURE check30();

create or replace function cash_back(x integer, y integer) returns numeric as
$$
declare
d date;
i numeric;
begin
select trip_id into i from trip_dates where id=y;
select starting_date into d from trip_dates where id=i;
if d-current_timestamp>3*interval'1 month' then
select paid_amount into i from client_trips where client_id=x and trip_id=y;
return i;
end if;
if d-current_timestamp>1*interval'1 month' then
select paid_amount into i from client_trips where client_id=x and trip_id=y;
return round(i/2,2);
end if;
return round(0,2);
end;
$$
language plpgsql;

create view payments as
select c.id "client id", c.name, c.surname, t.name "trip name", ct.paid_amount, real_price(c.id, td.id) "price", ct.id "id"
from clients c join client_trips ct on c.id=ct.client_id join trip_dates td on ct.trip_id=td.id
join trips t on td.trip_id=t.id;

create view pilot_trips as
select p.id, p.name, p.surname, t.name "trip name", td.starting_date "starts", (td.starting_date+t.length*interval'1 day')::date "ends"  
from pilots p join trip_dates td on p.id=td.pilot_id join trips t on td.trip_id=t.id;

create view trip_attractions_list as
select t.id, t.name "trip name", a.name "attraction name", a.type, a.price, 
case when ta.mandatory=true then 'Yes' else 'No' end "mandatory"from
trips t join trip_attractions ta on t.id=ta.trip_id join attractions a on ta.attraction_id=a.id order by t.id;

create view client_discounts as
select c.id, c.name, c.surname, t.name "trip name", sum_discounts(c.id, td.id) "discounts"
from clients c join client_trips ct on c.id=ct.client_id join trip_dates td on ct.trip_id=td.id
join trips t on td.trip_id=t.id;

create view client_cash_back as
select c.id, c.name, c.surname, t.name "trip name", cash_back(c.id, td.id) "cash back"
from clients c join client_trips ct on c.id=ct.client_id join trip_dates td on ct.trip_id=td.id
join trips t on td.trip_id=t.id;

create view client_opinions as
select c.id, c.name, c.surname, opinion
from clients c right outer join opinions ct on c.id=ct.client_id;
commit;