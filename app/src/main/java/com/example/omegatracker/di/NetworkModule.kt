package com.example.omegatracker.di

import com.example.omegatracker.data.EmptyListToNull
import com.example.omegatracker.data.YouTrackAPIService
import com.example.omegatracker.entity.Value
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
class NetworkModule {

    @Singleton
    @Provides
    fun provideYouTrackApiService(): YouTrackAPIService {
        return YouTrackAPIService()
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().build()
    }

    @Singleton
    @Provides
    fun provideMoshi(
        polymorphicAdapter: PolymorphicJsonAdapterFactory<Value>
    ): Moshi {
        return Moshi.Builder()
            .add(EmptyListToNull())
            .add(polymorphicAdapter)
            //.add(StateJsonAdapterFactory())
            .add(KotlinJsonAdapterFactory())
            .build()
    }


    @Singleton
    @Provides
    fun providePolymorphicFactoryForValue(): PolymorphicJsonAdapterFactory<Value> {
        return PolymorphicJsonAdapterFactory.of(Value::class.java, "\$type")
            .withSubtype(Value.State::class.java, "StateBundleElement")
            .withSubtype(Value.Period::class.java, "PeriodValue")
            .withDefaultValue(Value.DefaultValue())
    }
//    @Singleton
//    @Provides
//    fun providePolymorphicFactoryForValue() : PolymorphicJsonAdapterFactory<Value> {
//        return PolymorphicJsonAdapterFactory.of(Value::class.java,"\$type")
////            .withSubtype(Value.Default::class.java,"SingleEnumIssueCustomField")
////            .withSubtype(Value.Default::class.java,"MultiEnumIssueCustomField")
////            .withSubtype(Value.Default::class.java,"SingleBuildIssueCustomField")
////            .withSubtype(Value.Default::class.java,"MultiBuildIssueCustomField")
//            .withSubtype(Value.Period::class.java,"PeriodValue")
////            .withSubtype(Value.Default::class.java,"SingleVersionIssueCustomField")
////            .withSubtype(Value.Default::class.java,"MultiVersionIssueCustomField")
////            .withSubtype(Value.Default::class.java,"SingleOwnedIssueCustomField")
////            .withSubtype(Value.Default::class.java,"MultiOwnedIssueCustomField")
////            .withSubtype(Value.Default::class.java,"SingleUserIssueCustomField")
////            .withSubtype(Value.Default::class.java,"MultiUserIssueCustomField")
////            .withSubtype(Value.Default::class.java,"SingleGroupIssueCustomField")
////            .withSubtype(Value.Default::class.java,"MultiGroupIssueCustomField")
////            .withSubtype(Value.Default::class.java,"SimpleIssueCustomField")
////            .withSubtype(Value.Default::class.java,"DateIssueCustomField")
//            .withSubtype(Value.State::class.java,"StateBundleElement")
////            .withSubtype(Value.Default::class.java,"TextIssueCustomField")
//    }
//
//    @Singleton
//    @Provides
//    fun providePolymorphicFactoryForIssueCustomField() : PolymorphicJsonAdapterFactory<CustomField> {
//        return PolymorphicJsonAdapterFactory.of(CustomField::class.java,"\$type")
//
//            .withSubtype(CustomField.IssueCustomFieldWithValue::class.java,"StateIssueCustomField")
//            .withSubtype(CustomField.IssueCustomFieldWithValue::class.java,"PeriodIssueCustomField")
//
//            //Value type is array
//            .withSubtype(CustomField.IssueCustomFieldArrayValue::class.java,"MultiEnumIssueCustomField")
//            .withSubtype(CustomField.IssueCustomFieldArrayValue::class.java,"MultiBuildIssueCustomField")
//            .withSubtype(CustomField.IssueCustomFieldArrayValue::class.java,"MultiVersionIssueCustomField")
//            .withSubtype(CustomField.IssueCustomFieldArrayValue::class.java,"MultiOwnedIssueCustomField")
//            .withSubtype(CustomField.IssueCustomFieldArrayValue::class.java,"MultiUserIssueCustomField")
//            .withSubtype(CustomField.IssueCustomFieldArrayValue::class.java,"MultiGroupIssueCustomField")
//
//            //Value type is not array or period, or state
//            .withDefaultValue(CustomField.DefaultIssueCustomField("",null))
//    }
}