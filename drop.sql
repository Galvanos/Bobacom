
    set client_min_messages = WARNING;

    alter table if exists composizione 
       drop constraint if exists fk_composizione_ingrediente;

    alter table if exists composizione 
       drop constraint if exists fk_composizione_prodotto;

    alter table if exists operazione_magazzino 
       drop constraint if exists fk_operazionemagazzino_ingrediente;

    alter table if exists ordine 
       drop constraint if exists fk_ordine_utente;

    alter table if exists ordine_prodotto 
       drop constraint if exists fk_ordine_prodotto;

    alter table if exists ordine_prodotto 
       drop constraint if exists fk_ordine_prodotto;

    alter table if exists prodotto_categorie 
       drop constraint if exists FKd9yh6jm4nqb4baamuihqw64fl;

    alter table if exists prodotto_categorie 
       drop constraint if exists FKd0smw11vyyv097ni9s132wka4;

    drop table if exists categoria cascade;

    drop table if exists composizione cascade;

    drop table if exists ingredienti cascade;

    drop table if exists operazione_magazzino cascade;

    drop table if exists ordine cascade;

    drop table if exists ordine_prodotto cascade;

    drop table if exists prodotto cascade;

    drop table if exists prodotto_categorie cascade;

    drop table if exists utente cascade;
