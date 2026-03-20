package org.delcom.helpers

import org.delcom.dao.RefreshTokenDAO
import org.delcom.dao.UserDAO
import org.delcom.entities.RefreshToken
import org.delcom.entities.User

fun userDAOToModel(dao: UserDAO) = User(
    id = dao.id.value.toString(),
    username = dao.username,
    password = dao.password,
    name = dao.name,
    about = dao.about ?: ""
)

fun refreshTokenDAOToModel(dao: RefreshTokenDAO) = RefreshToken(
    id = dao.id.value.toString(),
    refreshToken = dao.token,
    userId = dao.userId.toString(),
    expiryDate = dao.expiryDate.toString()
)