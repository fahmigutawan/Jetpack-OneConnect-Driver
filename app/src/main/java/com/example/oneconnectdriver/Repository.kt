package com.example.oneconnectdriver

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.oneconnectdriver.model.domain.EmTransportModel
import com.example.oneconnectdriver.model.struct.EmCallStruct
import com.example.oneconnectdriver.model.struct.FcmTokenStruct
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class Repository @Inject constructor(
    private val context: Context
) {
    val firestore = FirebaseFirestore.getInstance()
    val realtimeDb = FirebaseDatabase.getInstance()
    val fcm = FirebaseMessaging.getInstance()
    val datastore = context.dataStore

    fun getAllEmTransport(
        onSuccess: (List<EmTransportModel>) -> Unit
    ) {
        val tmp = ArrayList<EmTransportModel>()
        firestore
            .collection("em_transport")
            .addSnapshotListener { value, error ->
                value?.let { valueNotNull ->
                    valueNotNull.forEach { valueNotNullItem ->
                        firestore
                            .collection("em_srv_provider")
                            .document(valueNotNullItem["em_pvd_id"] as String)
                            .addSnapshotListener { value2, error2 ->
                                value2?.let { value2NotNull ->
                                    tmp.add(
                                        EmTransportModel(
                                            em_transport_id = valueNotNullItem["em_transport_id"] as String,
                                            em_pvd_id = valueNotNullItem["em_pvd_id"] as String,
                                            is_available = valueNotNullItem["is_available"] as Boolean,
                                            regist_number = valueNotNullItem["regist_number"] as String,
                                            pvd_name = value2NotNull["name"] as String
                                        )
                                    )

                                    if (tmp.size == valueNotNull.size()) {
                                        onSuccess(tmp)
                                    }
                                }
                            }
                    }
                }
            }
    }

    fun getEmTransportById(
        emTransportId: String,
        onSuccess: (EmTransportModel) -> Unit
    ) {
        firestore
            .collection("em_transport")
            .document(emTransportId)
            .addSnapshotListener { value, error ->
                value?.let { valueNotNullItem ->
                    firestore
                        .collection("em_srv_provider")
                        .document(valueNotNullItem["em_pvd_id"] as String)
                        .addSnapshotListener { value2, error2 ->
                            value2?.let { value2NotNull ->
                                onSuccess(
                                    EmTransportModel(
                                        em_transport_id = valueNotNullItem["em_transport_id"] as String,
                                        em_pvd_id = valueNotNullItem["em_pvd_id"] as String,
                                        is_available = valueNotNullItem["is_available"] as Boolean,
                                        regist_number = valueNotNullItem["regist_number"] as String,
                                        pvd_name = value2NotNull["name"] as String
                                    )
                                )
                            }
                        }
                }
            }
    }

    suspend fun login(
        emTransportId: String
    ) {
        datastore.edit {
            it[stringPreferencesKey("em_transport_id")] = emTransportId
        }
    }

    suspend fun logout() {
        datastore.edit {
            it[stringPreferencesKey("em_transport_id")] = ""
        }
    }

    suspend fun isLogin() = datastore.data.map {
        it[stringPreferencesKey("em_transport_id")] ?: ""
    }.first() != ""

    suspend fun getTransportId() = datastore.data.map {
        it[stringPreferencesKey("em_transport_id")] ?: ""
    }.first()

    suspend fun updateFcmToken() {
        val id = getTransportId()

        fcm.token.addOnSuccessListener { token ->
            firestore
                .collection("fcm_token")
                .document(id)
                .set(
                    FcmTokenStruct(
                        id,
                        token
                    )
                )
        }
    }

    suspend fun deleteFcmToken(
        onSuccess: () -> Unit
    ) {
        val id = getTransportId()

        firestore
            .collection("fcm_token")
            .document(id)
            .delete()
            .addOnSuccessListener {
                onSuccess()
            }
    }

    suspend fun getEmCall(
        onListened: (List<EmCallStruct>) -> Unit
    ) {
        val id = getTransportId()

        realtimeDb
            .reference
            .child("em_call")
            .orderByChild("em_transport_id")
            .equalTo(id)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    onListened(
                        snapshot.children.map {
                            EmCallStruct(
                                em_call_id = it.child("em_call_id").value.toString(),
                                em_call_status_id = it.child("em_call_status_id").value.toString(),
                                em_pvd_id = it.child("em_pvd_id").value.toString(),
                                em_transport_id = it.child("em_transport_id").value.toString(),
                                user_phone_number = it.child("user_phone_number").value.toString(),
                                uid = it.child("uid").value.toString(),
                                user_lat = it.child("user_lat").value.toString(),
                                user_long = it.child("user_long").value.toString(),
                                created_at = it.child("created_at").value as Long
                            )
                        }.sortedBy {
                            it.created_at
                        }
                    )
                }

                override fun onCancelled(error: DatabaseError) {
                    //TODO
                }

            })
    }

    fun updateCallStatus(
        emCallId: String,
        status: String,
        onSuccess: () -> Unit
    ) {
        realtimeDb
            .reference
            .child("em_call")
            .child(emCallId)
            .child("em_call_status_id")
            .setValue(status)
            .addOnSuccessListener {
                onSuccess()
            }
    }

    suspend fun updateTransportAvailability(status: Boolean, onSuccess: () -> Unit) {
        val id = getTransportId()

        firestore
            .collection("em_transport")
            .document(id)
            .update("is_available", status)
            .addOnSuccessListener {
                onSuccess()
            }
    }

    fun updateLocationLiveTracking(
        em_call_id: String,
        long: Double,
        lat: Double
    ) {
        realtimeDb
            .reference
            .child("em_call")
            .child(em_call_id)
            .updateChildren(
                mapOf(
                    "transport_long" to long.toString(),
                    "transport_lat" to lat.toString()
                )
            )
    }
}