case class Torneo (listaPostas : List[Posta], listaDragones : List[Dragon], regla : ReglaEstandar = new ReglaEstandar) {

  def jugarTorneo[Inscripto](inscriptos: List[Inscripto], regla: Regla[Inscripto]): Option[Inscripto]={
    val participantesFinales = listaPostas.foldLeft(inscriptos: List[Inscripto])((participantesRestantes: List[Inscripto], postaActual: Posta) => {
      val participantesListos = regla.realizarEleccion(participantesRestantes, listaDragones, postaActual)
      regla.eliminarParticipantes(participantesListos)
    })

    participantesFinales.size match{
      case 0 => None
      case 1 => Some(participantesFinales.head)
      case _ => Some(regla.desempatar(participantesFinales))
    }
  }
}

// @todo
// la regla  se podria recibir por parametro