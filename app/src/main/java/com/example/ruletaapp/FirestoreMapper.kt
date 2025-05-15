package com.example.ruletaapp

object FirestoreMapper {

    @JvmStatic
    fun convertirResposta(resposta: FirestoreResponse): List<Puntuacio> {
        return resposta.documents.mapNotNull { doc ->
            val email = doc.fields.email.stringValue ?: return@mapNotNull null
            val monedes = doc.fields.monedes.integerValue?.toIntOrNull() ?: return@mapNotNull null
            val timestamp = doc.fields.timestamp.integerValue?.toLongOrNull() ?: 0L

            Puntuacio(email, monedes, timestamp)
        }
    }
}
