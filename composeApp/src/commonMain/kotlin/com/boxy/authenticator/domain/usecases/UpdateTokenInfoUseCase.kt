package com.boxy.authenticator.domain.usecases

import com.boxy.authenticator.data.models.Thumbnail
import com.boxy.authenticator.domain.repository.TokenRepository

class UpdateTokenInfoUseCase(
    private val tokenRepository: TokenRepository,
) {
    operator fun invoke(tokenId: String, issuer: String, label: String, thumbnail: Thumbnail) =
        runCatching {
            tokenRepository.updateToken(
                tokenId = tokenId,
                issuer = issuer,
                label = label,
                thumbnail = thumbnail,
            )
        }
}