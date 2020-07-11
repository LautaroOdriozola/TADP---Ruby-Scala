#la exception que tira cuando llama un metodo en conflicto
class MetodoEnConflicto < Exception
end


#las clases tienen las estrategias
class Class
  attr_accessor :estrategias
end

#definicion de trait
class Trait
  attr_accessor :metodos, :metodos_conflictivos

  def initialize(metodos={},metodos_conflictivos={})
    @metodos = metodos
    @metodos_conflictivos = metodos_conflictivos
  end

  def self.define(&block)
    trait = new
    trait.instance_eval(&block)
    trait
  end

  def name(nombre_de_trait)
    Object.const_set(nombre_de_trait,self)
  end

  def method(metodo, &block)
    @metodos[metodo] = block
  end

  def escribir(clase)
    clase.estrategias = {}
    @metodos.each do |key, value|
      clase.define_method(key,value) unless clase.instance_methods(false).include? key
    end
    @metodos_conflictivos.each { |key, value| clase.define_method(key, proc do |*args|
      raise MetodoEnConflicto.new unless self.class.estrategias[key]
      un_proc = self.class.estrategias[key].call(value)
      instance_exec *args, &un_proc
    end ) unless clase.instance_methods(false).include? key}
  end

  def +(trait)
    nuevos_metodos = {}
    nuevos_metodos_conflictivos = {}

    @metodos.each do |key, value|
      if trait.metodos.has_key? key
        nuevos_metodos_conflictivos[key]=[value,trait.metodos[key]]
      elsif trait.metodos_conflictivos.has_key? key
        nuevos_metodos_conflictivos[key]= [value].concat(trait.metodos_conflictivos[key])
      else
        nuevos_metodos[key]=value
      end
    end

    @metodos_conflictivos.each do |key, value|
      if trait.metodos.has_key? key
        nuevos_metodos_conflictivos[key]= value.concat([trait.metodos[key]])
      elsif trait.metodos_conflictivos.has_key? key
        nuevos_metodos_conflictivos[key]= value.concat(trait.metodos_conflictivos[key])
      else
        nuevos_metodos_conflictivos[key]=value
      end
    end

    trait.metodos.each do |key, value|
      nuevos_metodos[key]=value unless nuevos_metodos_conflictivos.has_key? key
    end

    trait.metodos_conflictivos.each do |key, value|
      nuevos_metodos_conflictivos[key]=value unless nuevos_metodos_conflictivos.has_key? key
    end

    Trait.new nuevos_metodos, nuevos_metodos_conflictivos
  end

  def -(metodo)
    nuevos_metodos = @metodos.clone
    nuevos_metodos_conflictivos = @metodos_conflictivos.clone

    nuevos_metodos.delete(metodo)
    nuevos_metodos_conflictivos.delete(metodo)

    Trait.new nuevos_metodos, nuevos_metodos_conflictivos
  end

  def <<(cambio)
    nuevos_metodos = @metodos.clone
    nuevos_metodos_conflictivos = @metodos_conflictivos.clone

    cambio.call(nuevos_metodos)
    cambio.call(nuevos_metodos_conflictivos)

    Trait.new nuevos_metodos, nuevos_metodos_conflictivos
  end
end


class Symbol
  def >>(metodo)
    proc do |arg| arg[metodo] = arg[self] if arg.has_key? self end
  end
end

class Module
  def uses(trait)
    trait.escribir(self)
  end
end




class Estrategia

  def self.por_orden_de_aparicion()
    proc do |metodos|
      proc do |*args| metodos.each{ |metodo| instance_exec *args, &metodo} end
    end
  end

  def self.tipo_fold(funcion)
    proc do |metodos|
      proc do |*args| metodos.map{|metodo| instance_exec *args, &metodo}.reduce(funcion) end
    end
  end

  def self.ultimo_que_cumple_condicion(&condicion)
    proc do |metodos|
      proc do |*args| metodos.map{|metodo| instance_exec *args, &metodo}.find &condicion end
    end
  end

end









