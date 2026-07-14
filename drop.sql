
    set client_min_messages = WARNING;

    alter table if exists composizione 
       drop constraint if exists fk_composizione_ingrediente;

    alter table if exists composizione 
       drop constraint if exists fk_composizione_prodotto;

    alter table if exists ingrediente_allergeni 
       drop constraint if exists FKt5tc4qlat5k66h0qi7qc62v8k;

    alter table if exists ingrediente_allergeni 
       drop constraint if exists FKl7ia407ebhgbgwcvq25fv72y0;

    alter table if exists ingredienti 
       drop constraint if exists FKdrp3f0eh035acny2845s84jud;

    alter table if exists operazione_magazzino 
       drop constraint if exists fk_operazionemagazzino_ingrediente;

    alter table if exists ordine 
       drop constraint if exists fk_ordine_utente;

    alter table if exists ordine_prodotto 
       drop constraint if exists fk_ordine_prodotto;

    alter table if exists ordine_prodotto 
       drop constraint if exists fk_prodotto_ordine;

    alter table if exists prodotto_promozione 
       drop constraint if exists FKqofkxk4to9kr92tqxnssq1tlj;

    alter table if exists prodotto_promozione 
       drop constraint if exists FK7rwhver15f0r79mi4fii21ywx;

    alter table if exists prodotto_tag 
       drop constraint if exists FKhjbnv15gqiaap7euksgmmb10t;

    alter table if exists prodotto_tag 
       drop constraint if exists FK4c4df3aai5yp50tttntjgeq7k;

    drop table if exists allergeni cascade;

    drop table if exists categoria_ingrediente cascade;

    drop table if exists composizione cascade;

    drop table if exists ingrediente_allergeni cascade;

    drop table if exists ingredienti cascade;

    drop table if exists operazione_magazzino cascade;

    drop table if exists ordine cascade;

    drop table if exists ordine_prodotto cascade;

    drop table if exists prodotto cascade;

    drop table if exists prodotto_promozione cascade;

    drop table if exists prodotto_tag cascade;

    drop table if exists promozione cascade;

    drop table if exists tag_prodotto cascade;

    drop table if exists Utente cascade;
