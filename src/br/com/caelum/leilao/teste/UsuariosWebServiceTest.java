package br.com.caelum.leilao.teste;

import br.com.caelum.leilao.modelo.Usuario;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.path.xml.XmlPath;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

import static com.jayway.restassured.RestAssured.get;

public class UsuariosWebServiceTest {

    private Usuario usuarioEsperado1;
    private Usuario usuarioEsperado2;

    @Before
    public void setUp(){
         usuarioEsperado1 = new Usuario(1L,"Mauricio Aniche", "mauricio.aniche@caelum.com.br");
         usuarioEsperado2 = new Usuario(2L,"Guilherme Silveira", "guilherme.silveira@caelum.com.br");

        RestAssured.baseURI = "http://localhost:8080";
    }

    @Test
    public void deveRetornarListaDeUsuario1XML(){
       XmlPath responsePath = given()
               .header("Accept", "application/xml")
               .get("/usuarios").andReturn().xmlPath();

        Usuario usuario1 = responsePath.getObject("list.usuario[0]", Usuario.class);
        Usuario usuario2 = responsePath.getObject("list.usuario[1]", Usuario.class);
        System.out.println(responsePath.get());

        assertEquals(usuario1, usuarioEsperado1);
        assertEquals(usuario2, usuarioEsperado2);
    }

    @Test
    public void deveRetornarListaDeUsuario2XML(){
        XmlPath responsePath = given()
                .header("Accept", "application/xml")
                .get("/usuarios").andReturn().xmlPath();


        System.out.println(responsePath.getString("list"));

        List<Usuario> usuarios = responsePath.getList("list.usuario", Usuario.class);

        assertEquals(usuarioEsperado1, usuarios.get(0));
        assertEquals(usuarioEsperado2, usuarios.get(1));
    }

    @Test
    public void deveRetornarOUsuarioPorId(){
        JsonPath path = given()
                .header("Accept", "application/json")
                .parameter("usuario.id", 1)
                .get("/usuarios/show")
                .andReturn()
                .jsonPath();

        System.out.println(path.getString(""));
        Usuario usuario =  path.getObject("usuario", Usuario.class);

        assertEquals(usuarioEsperado1, usuario);
    }

    @Test
    public void deveAdicionarUmUsuario(){
        Usuario felipe = new Usuario("Felipe Rodrigues Deleta4", "felipe@felipe");

        XmlPath path =  given()
                .header("Accept", "application/xml")
                .contentType("application/xml")
                .body(felipe)
                .expect()
                .statusCode(200)
                .when()
                .post("usuarios")
                .andReturn()
                .xmlPath();

        Usuario resposta = path.getObject("usuario", Usuario.class);

        assertEquals("Felipe Rodrigues Deleta4", resposta.getNome());
        assertEquals("felipe@felipe", resposta.getEmail());

//        given().parameter("usario.id", resposta.getId()).delete("usuarios/deleta").andReturn().xmlPath();

        given()
                .contentType("application/xml").body(resposta)
                .expect().statusCode(200)
                .when().delete("/usuarios/deleta").andReturn().asString();
    }
}
