
         
CREATE TABLE CACTUS_SAFI.SWB_CAMPANIA_NOTIFICAR_LOG
(	
    COD_NOTIFICACION 		NUMBER(19) GENERATED ALWAYS AS IDENTITY, 
    COD_CAMPANIA 		    NUMBER(19),
    TIP_ENVIO 				VARCHAR2(50),
    NUM_TELEFONO 		    VARCHAR2(50),
    DIR_EMAIL 		        VARCHAR2(255),
    DES_ERROR 				VARCHAR2(500),
    FEC_ENVIO 			    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FEC_CREACION 			TIMESTAMP DEFAULT CURRENT_TIMESTAMP
)
tablespace CACTUS_SAFI
pctfree 10
initrans 1
maxtrans 255
storage
(
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
);

-- Add comments to the table 
comment on table CACTUS_SAFI.SWB_CAMPANIA_NOTIFICAR_LOG is 'Tabla para registrar personas notificaciones enviadas a los colaboradores';
comment on column CACTUS_SAFI.SWB_CAMPANIA_NOTIFICAR_LOG.COD_NOTIFICACION is 'Identificador único';
comment on column CACTUS_SAFI.SWB_CAMPANIA_NOTIFICAR_LOG.COD_CAMPANIA is 'Identificador de la campania asociada';
comment on column CACTUS_SAFI.SWB_CAMPANIA_NOTIFICAR_LOG.TIP_ENVIO is 'Tipo de envio';  
comment on column CACTUS_SAFI.SWB_CAMPANIA_NOTIFICAR_LOG.NUM_TELEFONO is 'Número de telefono'; 
comment on column CACTUS_SAFI.SWB_CAMPANIA_NOTIFICAR_LOG.DIR_EMAIL is 'Dirección de email';     
comment on column CACTUS_SAFI.SWB_CAMPANIA_NOTIFICAR_LOG.DES_ERROR is 'Descripción del error';
comment on column CACTUS_SAFI.SWB_CAMPANIA_NOTIFICAR_LOG.FEC_ENVIO is 'Fecha de envio';
comment on column CACTUS_SAFI.SWB_CAMPANIA_NOTIFICAR_LOG.FEC_CREACION is 'Fecha de creación';

-- Create/Recreate primary, unique and foreign key constraints 
alter table CACTUS_SAFI.SWB_CAMPANIA_NOTIFICAR_LOG
add constraint SWB_CAMPANIA_NOTIFICAR_LOG_PK primary key (COD_NOTIFICACION)
using index 
tablespace CACTUS_SAFI
pctfree 10
initrans 2
maxtrans 255
storage
(
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
); 

alter table CACTUS_SAFI.SWB_CAMPANIA_NOTIFICAR_LOG
add constraint SWB_CAMPANIA_NOTIFICAR_LOG_FK_01 foreign key (COD_CAMPANIA)
references CACTUS_SAFI.SWB_DATA_CAMPANIA (COD_CAMPANIA);

-- Grant/Revoke object privileges 
grant select, insert, update, delete on CACTUS_SAFI.SWB_CAMPANIA_NOTIFICAR_LOG to WEBUSER;

