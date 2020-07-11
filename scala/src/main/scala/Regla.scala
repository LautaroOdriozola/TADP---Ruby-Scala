trait Regla [Inscripto] {
  type Equipo = List[Participante]

  def eliminarParticipantes(listaParticipantes: List[Inscripto]) : List[Inscripto]
  def desempatar(participantesFinales: List[Inscripto]): Inscripto
  def realizarEleccion (listaParticipantes : List[Inscripto], listaDeDragones : List[Dragon], posta : Posta):List[Inscripto]
  def puedeSeleccionarDragon(participante: Participante) : Boolean
  def desmontarParticipantes(listaInscriptos: List[Participante]) : List[Participante] ={
    listaInscriptos.map(participante => participante.desmontarse())
  }

  def prepararParticipantes(listaParticipantes : List[Participante], listaDeDragones : List[Dragon], posta : Posta): List[Participante] ={
    var dragonesDisponibles = listaDeDragones
    listaParticipantes.foldLeft(List[Participante]()) ((listaParcial : List[Participante], participanteActual: Participante)=>{
      if(puedeSeleccionarDragon(participanteActual)) {
        val participantePreparado = participanteActual.mejorMontura(posta, dragonesDisponibles)
        participantePreparado.monturaActual().foreach( (dragonDeJinete : Dragon) => dragonesDisponibles = dragonesDisponibles.filterNot(dragon => dragon == dragonDeJinete))
        listaParcial :+ participantePreparado
      }else listaParcial  :+ participanteActual
    })
  }
}

class ReglaEstandar extends Regla[Participante]{
  def eliminarParticipantes(listaParticipantes : List[Participante]): List[Participante] ={
    desmontarParticipantes(listaParticipantes.dropRight(listaParticipantes.size / 2))
  }

  def desempatar(participantesFinales: List[Participante]): Participante = participantesFinales.head

  def puedeSeleccionarDragon(participante: Participante) = true

  def realizarEleccion (listaParticipantes : List[Participante], listaDeDragones : List[Dragon], posta : Posta):List[Participante] = {
    posta.iniciarPosta(prepararParticipantes(listaParticipantes, listaDeDragones, posta))
  }
}

case class ReglaEliminacion(cuantosEliminar : Int) extends ReglaEstandar{
  override def eliminarParticipantes(listaParticipantes: List[Participante]): List[Participante] = {
    desmontarParticipantes(listaParticipantes.dropRight(cuantosEliminar))
  }
}

case object TorneoInverso extends ReglaEstandar {
  override def eliminarParticipantes(listaParticipantes: List[Participante]): List[Participante] = {
    desmontarParticipantes(listaParticipantes.drop(listaParticipantes.size / 2))
  }

  override def desempatar(participantesFinales: List[Participante]): Participante = participantesFinales.last
}

case class ConBan(condicion : Participante=>Boolean) extends ReglaEstandar{
  override def puedeSeleccionarDragon(participante: Participante): Boolean = condicion(participante)
}

case object ConHandicap extends ReglaEstandar{
  override def realizarEleccion (listaParticipantes : List[Participante], listaDeDragones : List[Dragon], posta : Posta):List[Participante] = {
    var dragonesDisponibles = listaDeDragones
    posta.iniciarPosta(listaParticipantes.foldRight(List[Participante]()) ((participanteActual: Participante, listaParcial : List[Participante])=>{
      if(puedeSeleccionarDragon(participanteActual)) {
        val participantePreparado = participanteActual.mejorMontura(posta, dragonesDisponibles)
        participantePreparado.monturaActual().foreach( (dragonDeJinete : Dragon) => dragonesDisponibles = dragonesDisponibles.filterNot(dragon => dragon == dragonDeJinete))
        listaParcial :+ participantePreparado
      }else listaParcial  :+ participanteActual
    }).reverse)
    //prepararParticipantes(listaParticipantes, listaDeDragones, posta).reverse
    }
}

class ReglaPorEquipos extends Regla[List[Participante]]{

  def eliminarParticipantes(listaDeListasDeParticipantes : List[Equipo]): List[Equipo] ={
    listaDeListasDeParticipantes.map((listaParticipantes : Equipo) => desmontarParticipantes(listaParticipantes.dropRight(listaParticipantes.size / 2)))
  }

  def realizarEleccion (listaDeListasDeParticipantes : List[Equipo], listaDeDragones : List[Dragon], posta : Posta):List[Equipo] = {
    listaDeListasDeParticipantes.foldLeft(List[Equipo]()) ((listasParciales : List[Equipo], participantesActuales: Equipo)=> {
      listasParciales :+ posta.iniciarPosta(prepararParticipantes(participantesActuales, listaDeDragones, posta))
    })
  }

  def puedeSeleccionarDragon(participante: Participante) = true

  def desempatar(listaDeListasDeParticipantes : List[Equipo]): Equipo = {
    listaDeListasDeParticipantes.maxBy((equipo: Equipo) => equipo.size)
  }
}