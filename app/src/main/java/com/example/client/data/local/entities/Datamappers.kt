package com.example.client.data.local.entities

import com.example.client.data.model.UserDTO
import com.example.client.domain.model.google.Location


fun LocationEntity.toLocation() = Location(
    name = name,
    address = address,
    lat = latitude,
    lng = longitude
)

fun Location.toEntity() = LocationEntity(
    name = name,
    address = address,
    latitude = lat,
    longitude = lng
)


fun UserEntity.toUserDTO()= UserDTO(
    username = username,
    rol = rol
)

fun UserDTO.toEntity() = UserEntity(
    username = username,
    rol = rol
)
