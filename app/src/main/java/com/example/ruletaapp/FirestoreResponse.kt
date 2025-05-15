package com.example.ruletaapp

data class FirestoreResponse(
    val documents: List<FirestoreDocument> = emptyList()
)

data class FirestoreDocument(
    val name: String = "",
    val fields: Fields = Fields()
)

data class Fields(
    val email: FirestoreField = FirestoreField(),
    val monedes: FirestoreField = FirestoreField(),
    val timestamp: FirestoreField = FirestoreField()
)

data class FirestoreField(
    val stringValue: String? = null,
    val integerValue: String? = null
)
fun FirestoreResponse.toPuntuacionsList(): List<Puntuacio> {
    return documents.mapNotNull { doc ->
        val email = doc.fields.email.stringValue ?: return@mapNotNull null
        val monedes = doc.fields.monedes.integerValue?.toIntOrNull() ?: return@mapNotNull null
        val timestamp = doc.fields.timestamp.integerValue?.toLongOrNull() ?: 0L

        Puntuacio(email, monedes, timestamp)
    }
}
