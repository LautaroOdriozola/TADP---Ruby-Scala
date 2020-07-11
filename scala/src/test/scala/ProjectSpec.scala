import org.scalatest.{FreeSpec, Matchers}

class ProjectSpec extends FreeSpec with Matchers {
  //case class StatsBase (peso : Double, velocidad : Double, barbarosidad : Int, danioBase : Double)
  val vikingoPesado = Vikingo(StatsBase(200, 40, 100, 100))
  val vikingoVeloz = Vikingo(StatsBase(50, 150, 50, 30))
  val vikingoBarbaro = Vikingo(StatsBase(60, 20, 150, 45))
  val vikingoConMuchoDanio = Vikingo(StatsBase(60, 20, 50, 300))
  val vikingoConArmaGrosa = Vikingo(StatsBase(60, 20, 50, 45), item = Some(Arma (danioExtra = 200)))
  val vikingoPocoGanador = Vikingo(StatsBase(80, 200, 160, 450))

  val equipoGanador = List(vikingoPesado, vikingoVeloz, vikingoBarbaro)
  val equipoPerdedor1 = List(vikingoConMuchoDanio, vikingoConArmaGrosa)
  val equipoPerdedor2 =  List(vikingoPocoGanador)

  val listaDeVikingos = List(vikingoPesado, vikingoVeloz, vikingoBarbaro)
  val listaDeEquipos = List(equipoGanador, equipoPerdedor1, equipoPerdedor2)

  val dragonRapido = Dragon(peso = 20, raza = FuriaNocturna(60, List(criterioFacilDeMontar)))
  val dragonDificilDeMontar = Dragon(peso = 200, raza = Gronckle(List(), 20))
  val dragonConDanio = Dragon (peso = 50, raza = NadderMortifero(List(criterioFacilDeMontar)))

  val listaDeDragones = List(dragonRapido, dragonConDanio, dragonDificilDeMontar)

  val postaCombateBarbarosidad100 = combate(100)
  val postaCombateBarbarosidad50 = combate()
  val postaPesca = pesca()
  val postaCarrera = carrera(30)

  val listaDePostas = List(postaCombateBarbarosidad100, postaPesca, postaCarrera)

  val reglaEstandar = new ReglaEstandar
  val reglaPorEquipos = new ReglaPorEquipos

  val torneoBasico = Torneo(listaDePostas, listaDeDragones)


  "Creacion jinete exitoso" - {
    "vikingoPesado deberia poder montar un dragon rapido" in{
      assert(vikingoPesado.montar(dragonRapido).isInstanceOf[Jinete])
    }
  }

  "VikingoVeloz no logra montar dragonDificilDeMontar" - {
    "deberia devolver un error" in {
      intercept[Exception] { vikingoVeloz.montar(dragonDificilDeMontar) }
    }
  }

  "VikingoPesado es mejor que vikingoVeloz en posta de combate" - {
    "vikingoPesado deberia ganarle a vikingoVeloz en la postaCombateBarbarosidad100" in {
      assert(vikingoPesado.esMejorQue(vikingoVeloz)(postaCombateBarbarosidad100))
    }
  }

  "VikingoVeloz es mejor que vikingoPesado en posta de carrera" -{
    "vikingoVeloz deberia ganarle a vikingoPesado en carrera" in{
      assert(vikingoVeloz.esMejorQue(vikingoPesado)(postaCarrera))
    }
  }

  "vikingoBarbaro es mejor que vikingoPesado en posta de pesca" -{
    "vikingoBarbaro deberia ganarle a vikingoPesado en pesca" in{
      assert(vikingoBarbaro.esMejorQue(vikingoPesado)(postaPesca))
    }
  }

  "Vikingo pesado le conviene llevar montura en el postaCombateBarbarosidad100" -{
    "vikingoPesado deberia volverse un jinete para competir en el combate" in{
      assert(vikingoPesado.mejorMontura(combate(), listaDeDragones).isInstanceOf[Jinete])
    }
  }

  "vikingoConArmaGrosa le conviene participar como vikingo en la postaPesca" -{
    "vikingoConArmaGrosa deberia participar tal como esta en la pesca" in {
      assert(vikingoConArmaGrosa.mejorMontura(postaPesca, listaDeDragones).isInstanceOf[Vikingo])
    }
  }

  "vikingoMasGanador gana torneoBasico" -{
    "vikingoMasGanador deberia ganar el torneoBasico" in {
      val ganador = torneoBasico.jugarTorneo(listaDeVikingos,reglaEstandar)
      ganador shouldBe Some(vikingoPesado.incrementarHambre(40))
    }
  }

  "EquipoGanador gana torneo por equipos" -{
    "equipo ganador deberia ser el campeon" in {
      val ganador = torneoBasico.jugarTorneo(listaDeEquipos,reglaPorEquipos)
      ganador shouldBe Some(List(vikingoPesado.incrementarHambre(40)))
    }
  }

}


