package com.boxy.authenticator.helpers.serializers

import com.boxy.authenticator.data.models.Thumbnail
import com.boxy.authenticator.data.models.otp.HotpInfo
import com.boxy.authenticator.data.models.otp.OtpInfo
import com.boxy.authenticator.data.models.otp.SteamInfo
import com.boxy.authenticator.data.models.otp.TotpInfo
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

val BoxySerializersModule = SerializersModule {
    polymorphic(Thumbnail::class) {
        subclass(Thumbnail.Color::class, Thumbnail.Color.serializer())
        subclass(Thumbnail.Icon::class, Thumbnail.Icon.serializer())
    }
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