package com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.mapper;

import com.marco.cloud_ecommerce_api.domain.user.User;
import com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.entity.UserJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public User toDomain(UserJpaEntity entity) {
        if (entity == null) return null;
        return new User(
                entity.getId(),
                entity.getEmail(),
                entity.getPasswordHash(),
                entity.getRoles(),
                entity.getStatus(),
                entity.getCartId(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
    public UserJpaEntity toJpaEntity(User domain) {
        if (domain == null) return null;
        return new UserJpaEntity(
                domain.getId(),
                domain.getEmail(),
                domain.getPasswordHash(),
                domain.getRoles(),
                domain.getStatus(),
                domain.getCartId(),
                domain.getCreatedAt(),
                domain.getUpdatedAt()
        );
    }
}

/*
 (28-04, 09:05 hrs)
 NOTAS DE ARQUITECTURA (Mapeo de Capas):

 1. Sincronía de Espejos: Al rehidratar objetos, el constructor All-Args debe
    mapear cada campo con su equivalente en la capa contraria para asegurar
    la integridad total del estado.

 2. Desacoplamiento de Estado:
    - User (Domain) tiene 8 campos (Estado de Negocio).
    - UserJpaEntity (Infra) tiene 9 campos (Estado de Persistencia).
    - El campo extra 'active' es una "bandera técnica" para Soft Delete.

 3. Lógica de Conversión: El Mapper es el responsable de traducir el
    'UserStatus' (Negocio) al booleano 'active' (Persistencia) y viceversa,
    usando métodos como domain.isActive().
*/

/*
(09:19 am)
1- COINCIDIR EL CONSTRUCTOR DOMAIN CON ENTITY:
Al llegar al 7° atributo de entity: this.active = active; No se encuentra en domain
¿Qué cresta hago?
**No lo invento, lo "calculo" o lo "extraigo".**
El hecho de que `active` no esté en el dominio como un campo `private boolean active`
no significa que el dato no exista. El dominio tiene la **verdad de negocio** (`status`),
y la entidad tiene la **verdad técnica** (active).

DISCREPANCIA DE ATRIBUTOS (Domain vs Entity):
    Al llegar al atributo 'active' en la Entity, que NO existe en el Domain:

    ¿QUÉ HAGO?: Le pido al Dominio que me dé una respuesta lógica basada
    en su estado actual.

    SOLUCIÓN: En lugar de pasar una variable directa, paso el resultado
    de un **method**: 'domain.isActive()'. (esto es sabroso)

    POR QUÉ: El Dominio decide si está activo según sus reglas (ej. si el
    status no es DEACTIVATED), y el Mapper traduce esa decisión al campo
    técnico que la base de datos necesita para el Soft Delete.
 */