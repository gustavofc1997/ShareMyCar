package com.gustavoforero.sharemycar.domain

class Trip {
    constructor(origen: String?, destino: String, fecha: String, hora: String, cupos: Int, precio: String, phone: String, name: String) {
        this.origen = origen
        this.destino = destino
        this.fecha = fecha
        this.hora = hora
        this.cupos = cupos
        this.precio = precio
        this.phone = phone
        this.name = name
    }

    constructor()


    var origen: String? = null
    var email: String? = null
    var destino: String? = null
    var fecha: String? = null
    var hora: String? = null
    var cupos: Int = 0
    var precio: String? = null
    var phone: String? = null
    var name: String? = null

}
