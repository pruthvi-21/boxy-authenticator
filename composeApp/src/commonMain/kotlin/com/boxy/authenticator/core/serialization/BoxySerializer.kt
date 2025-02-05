package com.boxy.authenticator.core.serialization

import com.boxy.authenticator.domain.models.otp.HotpInfo
import com.boxy.authenticator.domain.models.otp.OtpInfo
import com.boxy.authenticator.domain.models.otp.SteamInfo
import com.boxy.authenticator.domain.models.otp.TotpInfo
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

val BoxySerializersModule = SerializersModule {
    polymorphic(OtpInfo::class) {
        subclass(HotpInfo::class, HotpInfo.serializer())
        subclass(TotpInfo::class, TotpInfo.serializer())
        subclass(SteamInfo::class, SteamInfo.serializer())
    }
}

val BoxyJson = Json {
    serializersModule = BoxySerializersModule
    classDiscriminator = "type"
    prettyPrint = true
}