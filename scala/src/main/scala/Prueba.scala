case object Prueba {

  def materia: String = "tadp"
  def pruebaPosta(): Unit = {
    val vikingo1 = Vikingo(StatsBase(150, 10, 50, 15))
    val vikingo2 = Vikingo(StatsBase(50, 25, 50, 30))
    val vikingo3 = Vikingo(StatsBase(60, 20, 50, 45))
    val listaDeVikingos = List(vikingo2, vikingo1, vikingo3)
    val posta1 = combate()
    val posta2 = pesca()
    val posta3 = carrera(15)
    val listaDePostas = List(posta1, posta2, posta3)
    val dragonRapido = Dragon(peso = 20, raza = FuriaNocturna(60, List(criterioFacilDeMontar)))
    val dragonConDanio = Dragon (peso = 50, raza = NadderMortifero(List(criterioFacilDeMontar)))
    val dragonDificilDeMontar = Dragon(peso = 200, raza = Gronckle(List()))
    val listaDeDragones = List(dragonRapido, dragonConDanio, dragonDificilDeMontar)

    val torneo = Torneo(listaDePostas, listaDeDragones)
    val torneoInverso = Torneo(listaDePostas, listaDeDragones, TorneoInverso)
    val torneoConHandicap = Torneo(listaDePostas, List[Dragon] (dragonRapido, dragonConDanio), ConHandicap)

    println("Resultados de posta de combate: \n" + posta1.competir(listaDeVikingos) + "\n")
    println("Resultados de posta de pesca: \n" + posta2.competir(listaDeVikingos) + "\n")
    println("Resultados de carrera: \n" + posta3.competir(listaDeVikingos) + "\n")

    println("Mejor montura para un combate: \n" + vikingo1.mejorMontura(combate(), listaDeDragones) + "\n")
    println("Mejor montura para una carrera: \n" + vikingo1.mejorMontura(carrera(35), listaDeDragones) + "\n")

/*    println(torneo.prepararParticipantes(listaDeVikingos, posta1))
    println(torneoConHandicap.prepararParticipantes(listaDeVikingos, posta1))

    println(torneo.jugarTorneo(listaDeVikingos))
    println(torneoInverso.jugarTorneo(listaDeVikingos))
    println(torneoConHandicap.jugarTorneo(listaDeVikingos))*/

  }

  def main(args: Array[String]): Unit = {
    pruebaPosta()
  }
}
