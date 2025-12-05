--
-- PostgreSQL database dump
--

\restrict d36FMZcfaGeGaNTaeVi1YKTVr3jy68AiqPQ6fwk4304mYLSV5YezirdHNbzHZvi

-- Dumped from database version 16.11 (Ubuntu 16.11-0ubuntu0.24.04.1)
-- Dumped by pg_dump version 16.11 (Ubuntu 16.11-0ubuntu0.24.04.1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: bestellung; Type: TABLE; Schema: public; Owner: dbuser
--

CREATE TABLE public.bestellung (
    id bigint NOT NULL,
    bestelldatum timestamp(6) without time zone NOT NULL,
    status character varying(255) NOT NULL,
    kunde_id bigint NOT NULL,
    CONSTRAINT bestellung_status_check CHECK (((status)::text = ANY ((ARRAY['NEU'::character varying, 'IN_BEARBEITUNG'::character varying, 'VERSENDET'::character varying, 'ABGESCHLOSSEN'::character varying, 'STORNIERT'::character varying])::text[])))
);


ALTER TABLE public.bestellung OWNER TO dbuser;

--
-- Name: bestellung_id_seq; Type: SEQUENCE; Schema: public; Owner: dbuser
--

CREATE SEQUENCE public.bestellung_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.bestellung_id_seq OWNER TO dbuser;

--
-- Name: bestellung_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dbuser
--

ALTER SEQUENCE public.bestellung_id_seq OWNED BY public.bestellung.id;


--
-- Name: bestellung_position; Type: TABLE; Schema: public; Owner: dbuser
--

CREATE TABLE public.bestellung_position (
    id bigint NOT NULL,
    einzelpreis numeric(10,2) NOT NULL,
    menge integer NOT NULL,
    bestellung_id bigint NOT NULL,
    produkt_id bigint NOT NULL
);


ALTER TABLE public.bestellung_position OWNER TO dbuser;

--
-- Name: bestellung_position_id_seq; Type: SEQUENCE; Schema: public; Owner: dbuser
--

CREATE SEQUENCE public.bestellung_position_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.bestellung_position_id_seq OWNER TO dbuser;

--
-- Name: bestellung_position_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dbuser
--

ALTER SEQUENCE public.bestellung_position_id_seq OWNED BY public.bestellung_position.id;


--
-- Name: kunde; Type: TABLE; Schema: public; Owner: dbuser
--

CREATE TABLE public.kunde (
    id bigint NOT NULL,
    email character varying(100) NOT NULL,
    geburtstag date,
    geschlecht character varying(20),
    hausnummer character varying(20),
    land character varying(100),
    mobilnummer character varying(20),
    name character varying(100) NOT NULL,
    ort character varying(100),
    postleitzahl character varying(10),
    strasse character varying(100),
    telefonnummer character varying(20),
    vorname character varying(100),
    CONSTRAINT kunde_geschlecht_check CHECK (((geschlecht)::text = ANY ((ARRAY['MAENNLICH'::character varying, 'WEIBLICH'::character varying, 'DIVERS'::character varying])::text[])))
);


ALTER TABLE public.kunde OWNER TO dbuser;

--
-- Name: kunde_id_seq; Type: SEQUENCE; Schema: public; Owner: dbuser
--

CREATE SEQUENCE public.kunde_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.kunde_id_seq OWNER TO dbuser;

--
-- Name: kunde_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dbuser
--

ALTER SEQUENCE public.kunde_id_seq OWNED BY public.kunde.id;


--
-- Name: produkt; Type: TABLE; Schema: public; Owner: dbuser
--

CREATE TABLE public.produkt (
    id bigint NOT NULL,
    beschreibung character varying(500),
    lagerbestand integer NOT NULL,
    name character varying(100) NOT NULL,
    preis numeric(10,2) NOT NULL
);


ALTER TABLE public.produkt OWNER TO dbuser;

--
-- Name: produkt_id_seq; Type: SEQUENCE; Schema: public; Owner: dbuser
--

CREATE SEQUENCE public.produkt_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.produkt_id_seq OWNER TO dbuser;

--
-- Name: produkt_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dbuser
--

ALTER SEQUENCE public.produkt_id_seq OWNED BY public.produkt.id;


--
-- Name: rechnung; Type: TABLE; Schema: public; Owner: dbuser
--

CREATE TABLE public.rechnung (
    id bigint NOT NULL,
    bezahltam timestamp(6) without time zone,
    bezahltvon character varying(255),
    erstellungsdatum timestamp(6) without time zone NOT NULL,
    gesamtbetrag numeric(38,2) NOT NULL,
    rechnungsnummer character varying(255) NOT NULL,
    status character varying(255) NOT NULL,
    bestellung_id bigint NOT NULL,
    CONSTRAINT rechnung_status_check CHECK (((status)::text = ANY ((ARRAY['OFFEN'::character varying, 'BEZAHLT'::character varying, 'STORNIERT'::character varying])::text[])))
);


ALTER TABLE public.rechnung OWNER TO dbuser;

--
-- Name: rechnung_id_seq; Type: SEQUENCE; Schema: public; Owner: dbuser
--

CREATE SEQUENCE public.rechnung_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.rechnung_id_seq OWNER TO dbuser;

--
-- Name: rechnung_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dbuser
--

ALTER SEQUENCE public.rechnung_id_seq OWNED BY public.rechnung.id;


--
-- Name: bestellung id; Type: DEFAULT; Schema: public; Owner: dbuser
--

ALTER TABLE ONLY public.bestellung ALTER COLUMN id SET DEFAULT nextval('public.bestellung_id_seq'::regclass);


--
-- Name: bestellung_position id; Type: DEFAULT; Schema: public; Owner: dbuser
--

ALTER TABLE ONLY public.bestellung_position ALTER COLUMN id SET DEFAULT nextval('public.bestellung_position_id_seq'::regclass);


--
-- Name: kunde id; Type: DEFAULT; Schema: public; Owner: dbuser
--

ALTER TABLE ONLY public.kunde ALTER COLUMN id SET DEFAULT nextval('public.kunde_id_seq'::regclass);


--
-- Name: produkt id; Type: DEFAULT; Schema: public; Owner: dbuser
--

ALTER TABLE ONLY public.produkt ALTER COLUMN id SET DEFAULT nextval('public.produkt_id_seq'::regclass);


--
-- Name: rechnung id; Type: DEFAULT; Schema: public; Owner: dbuser
--

ALTER TABLE ONLY public.rechnung ALTER COLUMN id SET DEFAULT nextval('public.rechnung_id_seq'::regclass);


--
-- Data for Name: bestellung; Type: TABLE DATA; Schema: public; Owner: dbuser
--

COPY public.bestellung (id, bestelldatum, status, kunde_id) FROM stdin;
\.


--
-- Data for Name: bestellung_position; Type: TABLE DATA; Schema: public; Owner: dbuser
--

COPY public.bestellung_position (id, einzelpreis, menge, bestellung_id, produkt_id) FROM stdin;
\.


--
-- Data for Name: kunde; Type: TABLE DATA; Schema: public; Owner: dbuser
--

COPY public.kunde (id, email, geburtstag, geschlecht, hausnummer, land, mobilnummer, name, ort, postleitzahl, strasse, telefonnummer, vorname) FROM stdin;
1	max@example.com	1975-08-17	MAENNLICH	1	Deutschland	\N	Mustermann	Berlin	12345	Musterstraße	0301234567	Max
2	anna@example.com	1980-03-22	WEIBLICH	2	Deutschland	01761234567	Schmidt	Hamburg	54321	Beispielweg	\N	Anna
3	mueller@example.com	1978-08-08	\N	33	\N	\N	Müller	Gütersloh	33332	Am Anger	\N	Anna
\.


--
-- Data for Name: produkt; Type: TABLE DATA; Schema: public; Owner: dbuser
--

COPY public.produkt (id, beschreibung, lagerbestand, name, preis) FROM stdin;
1	Business Laptop mit 16GB RAM	10	Laptop	899.99
2	Kabellose Gaming-Maus	50	Maus	49.99
3	Mechanische RGB-Tastatur	25	Tastatur	129.99
\.


--
-- Data for Name: rechnung; Type: TABLE DATA; Schema: public; Owner: dbuser
--

COPY public.rechnung (id, bezahltam, bezahltvon, erstellungsdatum, gesamtbetrag, rechnungsnummer, status, bestellung_id) FROM stdin;
\.


--
-- Name: bestellung_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dbuser
--

SELECT pg_catalog.setval('public.bestellung_id_seq', 1, false);


--
-- Name: bestellung_position_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dbuser
--

SELECT pg_catalog.setval('public.bestellung_position_id_seq', 1, false);


--
-- Name: kunde_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dbuser
--

SELECT pg_catalog.setval('public.kunde_id_seq', 3, true);


--
-- Name: produkt_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dbuser
--

SELECT pg_catalog.setval('public.produkt_id_seq', 3, true);


--
-- Name: rechnung_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dbuser
--

SELECT pg_catalog.setval('public.rechnung_id_seq', 1, false);


--
-- Name: bestellung bestellung_pkey; Type: CONSTRAINT; Schema: public; Owner: dbuser
--

ALTER TABLE ONLY public.bestellung
    ADD CONSTRAINT bestellung_pkey PRIMARY KEY (id);


--
-- Name: bestellung_position bestellung_position_pkey; Type: CONSTRAINT; Schema: public; Owner: dbuser
--

ALTER TABLE ONLY public.bestellung_position
    ADD CONSTRAINT bestellung_position_pkey PRIMARY KEY (id);


--
-- Name: kunde kunde_pkey; Type: CONSTRAINT; Schema: public; Owner: dbuser
--

ALTER TABLE ONLY public.kunde
    ADD CONSTRAINT kunde_pkey PRIMARY KEY (id);


--
-- Name: produkt produkt_pkey; Type: CONSTRAINT; Schema: public; Owner: dbuser
--

ALTER TABLE ONLY public.produkt
    ADD CONSTRAINT produkt_pkey PRIMARY KEY (id);


--
-- Name: rechnung rechnung_pkey; Type: CONSTRAINT; Schema: public; Owner: dbuser
--

ALTER TABLE ONLY public.rechnung
    ADD CONSTRAINT rechnung_pkey PRIMARY KEY (id);


--
-- Name: rechnung uk_1uo3480eqbia9xnsonnbl93ag; Type: CONSTRAINT; Schema: public; Owner: dbuser
--

ALTER TABLE ONLY public.rechnung
    ADD CONSTRAINT uk_1uo3480eqbia9xnsonnbl93ag UNIQUE (rechnungsnummer);


--
-- Name: rechnung uk_eg64o9br54q65m1obn19dmsj6; Type: CONSTRAINT; Schema: public; Owner: dbuser
--

ALTER TABLE ONLY public.rechnung
    ADD CONSTRAINT uk_eg64o9br54q65m1obn19dmsj6 UNIQUE (bestellung_id);


--
-- Name: kunde uk_q5r7vvlcftgi2ci4tv4258aui; Type: CONSTRAINT; Schema: public; Owner: dbuser
--

ALTER TABLE ONLY public.kunde
    ADD CONSTRAINT uk_q5r7vvlcftgi2ci4tv4258aui UNIQUE (email);


--
-- Name: bestellung_position fk56wbk32nqd9m8oatn6gm10squ; Type: FK CONSTRAINT; Schema: public; Owner: dbuser
--

ALTER TABLE ONLY public.bestellung_position
    ADD CONSTRAINT fk56wbk32nqd9m8oatn6gm10squ FOREIGN KEY (bestellung_id) REFERENCES public.bestellung(id);


--
-- Name: bestellung fk60a1yda6p7wck2bax2lvd1oq5; Type: FK CONSTRAINT; Schema: public; Owner: dbuser
--

ALTER TABLE ONLY public.bestellung
    ADD CONSTRAINT fk60a1yda6p7wck2bax2lvd1oq5 FOREIGN KEY (kunde_id) REFERENCES public.kunde(id);


--
-- Name: rechnung fk6397ma8dlr4wd8fafixl0nxb0; Type: FK CONSTRAINT; Schema: public; Owner: dbuser
--

ALTER TABLE ONLY public.rechnung
    ADD CONSTRAINT fk6397ma8dlr4wd8fafixl0nxb0 FOREIGN KEY (bestellung_id) REFERENCES public.bestellung(id);


--
-- Name: bestellung_position fkce235bruwy0y40qmk3j8word6; Type: FK CONSTRAINT; Schema: public; Owner: dbuser
--

ALTER TABLE ONLY public.bestellung_position
    ADD CONSTRAINT fkce235bruwy0y40qmk3j8word6 FOREIGN KEY (produkt_id) REFERENCES public.produkt(id);


--
-- Name: SCHEMA public; Type: ACL; Schema: -; Owner: pg_database_owner
--

GRANT ALL ON SCHEMA public TO dbuser;


--
-- Name: DEFAULT PRIVILEGES FOR SEQUENCES; Type: DEFAULT ACL; Schema: public; Owner: dbuser
--

ALTER DEFAULT PRIVILEGES FOR ROLE dbuser IN SCHEMA public GRANT ALL ON SEQUENCES TO dbuser;


--
-- Name: DEFAULT PRIVILEGES FOR TABLES; Type: DEFAULT ACL; Schema: public; Owner: dbuser
--

ALTER DEFAULT PRIVILEGES FOR ROLE dbuser IN SCHEMA public GRANT ALL ON TABLES TO dbuser;


--
-- PostgreSQL database dump complete
--

\unrestrict d36FMZcfaGeGaNTaeVi1YKTVr3jy68AiqPQ6fwk4304mYLSV5YezirdHNbzHZvi

