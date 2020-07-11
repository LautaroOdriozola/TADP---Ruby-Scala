describe Prueba do
  let(:prueba) { Prueba.new }

  describe '#materia' do
    it 'debería pasar este test' do
      expect(prueba.materia).to be :tadp
    end
  end
end


describe Trait do
  describe "#uses" do
    before do
      Trait.define do
        name :MiTrait
        method :metodo1 do
          "Hola"
        end
        method :metodo2 do |un_numero|
          un_numero*0+42
        end
      end

      class MiClase
        def metodo1
          "mundo"
        end
        uses MiTrait
      end

      @obj = MiClase.new
    end

    describe 'que esten los metodos en la clase' do
      it 'se llama a metodo1 (no lo sobre escribe)' do
        expect(@obj.metodo1).to eq "mundo"
      end
      it 'se llama a metodo2 con argumento' do
        expect(@obj.metodo2(33)).to eq 42
      end
    end
  end

  describe '#uses con conflicto' do
    before do
      Trait.define do
        name :MiTrait
        method :metodo1 do
          "Hola"
        end
        method :metodo2 do |un_numero|
          un_numero*0+42
        end
      end

      Trait.define do
        name :MiOtroTrait
        method :metodo1 do
          "kawuabonga"
        end
        method :metodo3 do
          "zaraza"
        end
      end

      class OtraClase
        uses MiTrait + MiOtroTrait
      end
      @objeto = OtraClase.new
    end
    describe 'que los metodos esten en la clase' do
      it 'llama a metodo2' do
        expect(@objeto.metodo2(84)).to eq 42
      end
      it "llama a metodo3" do
        expect(@objeto.metodo3).to eq "zaraza"
      end

      #Como agarrar una exception con RSpec
      it "llama a metodo1 y tira exception" do
        expect{@objeto.metodo1}.to raise_error MetodoEnConflicto
      end
    end

    describe "se setea una estrategia" do
      it 'se toma el primer trait' do
        estrategia = proc do |lista| lista[0] end
        OtraClase.estrategias[:metodo1]=estrategia

        expect(@objeto.metodo1).to eq "Hola"
      end
      it 'se toma el segundo trait' do
        estrategia = proc do |lista| lista[1] end
        OtraClase.estrategias[:metodo1]=estrategia

        expect(@objeto.metodo1).to eq "kawuabonga"
      end
    end
  end

  describe "#<<" do
    before do
      Trait.define do
        name :TestOperadorRenombrar
        method :metodo1 do
          "Hola"
        end
        method :metodo2 do |un_numero|
          un_numero*0+42
        end
      end

      class ClassTestOperadorRenombrar
        uses TestOperadorRenombrar<<(:metodo1 >> :saludar)
      end
      @objeto = ClassTestOperadorRenombrar.new
    end
    describe "cambio de nombre" do
      it "" do
        expect(@objeto.saludar).to eq "Hola"
      end
    end
  end
  describe "#-" do
    before do
      Trait.define do
        name :TestOperadorResta
        method :metodo1 do
          "Hola"
        end
        method :metodo2 do |un_numero|
          un_numero*0+42
        end
      end

      class ClassTestOperadorResta
        uses TestOperadorResta - :metodo1
      end
      @objeto = ClassTestOperadorResta.new
    end
    describe "se resta el metodo1" do
      it "se llama metodo1" do
        expect {@objeto.metodo1}.to raise_error NoMethodError
      end

      it 'se llama metodo2' do
        expect(@objeto.metodo2(42)).to eq 42
      end
    end
  end

  describe "estrategias" do

    before do
      Trait.define do
        name :Trait1
        method :metodo1 do puts "Hello" end
        method :metodo2 do 1 end
      end
      Trait.define do
        name :Trait2
        method :metodo1 do puts "Hola" end
        method :metodo2 do 2 end
      end
      Trait.define do
        name :Trait3
        method :metodo1 do puts "Alo" end
        method :metodo2 do 3 end
      end

      class UnaClaseMas
        uses Trait1+Trait2+Trait3
      end

      @objeto = UnaClaseMas.new

    end

    describe "llamo metodo1" do
      it 'debe tirar error' do
        expect{@objeto.metodo1}.to raise_error MetodoEnConflicto
      end

      it 'debe imprimir todos los saludos' do
        #estrategia que ejecuta todos los mensajes
        UnaClaseMas.estrategias[:metodo1] = Estrategia.por_orden_de_aparicion

        expect{@objeto.metodo1}.not_to raise_error
      end

      it 'debe devolver el resultado de un fold sobre todos los metodos conflictivos' do
        #estrategia que ejecuta la lista como un fold
        UnaClaseMas.estrategias[:metodo2] = Estrategia.tipo_fold :+

        expect(@objeto.metodo2).to eq 6
      end

      it 'debe devolver el ultimo valor valido evaluado sobre los metodos conflictivos' do
        #estrategia que ejecuta la lista con una condicion
        UnaClaseMas.estrategias[:metodo2] = Estrategia.ultimo_que_cumple_condicion do |x| x>2 end

        expect(@objeto.metodo2).to eq 3
      end

    end

  end
end