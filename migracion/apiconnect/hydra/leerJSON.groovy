import groovy.json.JsonSlurper
import groovy.json.JsonBuilder  
import java.io.File

static main(args) {
  def json=new JsonSlurper()
  def archivo=new File("client.json")

  if(archivo.exists()){
    println "Archivo existe:${archivo.getAbsolutePath()}"
    def objeto=json.parse(archivo) 
    if(objeto !=null){
        if(objeto.id == null && objeto.id == null){
            println "Objetos nulos"
        }
        println "ID:"+objeto.id+" Secreto:"+objeto.client_secret
    }
    objeto.id="hola hombre"
    def obJSON = new JsonBuilder(objeto).toPrettyString()
    def target = new File("volcado.json")

    println obJSON

    target.withWriter { file ->
        obJSON.eachLine { line ->
            file.writeLine(line)
        }
    }
  }
  //new File("volcado.json").write( JsonBuilder(objeto).toPrettyString() )

  /*
  def source = new File("client.json")
  def target = new File("volcado.json")

  target.withWriter { file ->
    source.eachLine { line ->
        file.writeLine(line)
    }
  }
  */
}