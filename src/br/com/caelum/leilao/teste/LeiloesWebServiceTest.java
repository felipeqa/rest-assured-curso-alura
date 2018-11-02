package br.com.caelum.leilao.teste;

import br.com.caelum.leilao.modelo.Leilao;
import br.com.caelum.leilao.modelo.Usuario;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.path.xml.XmlPath;
import org.junit.Test;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

public class LeiloesWebServiceTest {

    @Test
    public void deveRetornarLeilaoPorId(){
        JsonPath path = given()
                .header("Accept", "application/json")
                .parameter("leilao.id", 1)
                .get("/leiloes/show")
                .andReturn()
                .jsonPath();

        Usuario usuario = new Usuario(1L, "Mauricio Aniche", "mauricio.aniche@caelum.com.br");

        Leilao leilaoEsperado = new Leilao(1L, "Geladeira", 800.00, usuario, false);

        System.out.println(path.getString(""));
        Leilao leilao =  path.getObject("leilao", Leilao.class);

        assertEquals(leilaoEsperado, leilao);
    }

    @Test
    public void deveRetornarQuantidadeDeLeiloes(){
        XmlPath path = given()
                .header("Accept", "application/xml")
                .get("/leiloes/total")
                .andReturn()
                .xmlPath();


        int quantidaLeiloes = path.getInt("int");
        System.out.println(quantidaLeiloes);

        assertEquals(quantidaLeiloes, 2);
    }

    @Test
    public void deveAdicionarUmLeilão(){
        Usuario felipe = new Usuario(1L,"Felipe Rodrigues Leilão", "felipe@felipe");
        Leilao leilao = new Leilao(1L, "fogao", 800.00, felipe, false);

        XmlPath path =  given()
                .header("Accept", "application/xml")
                .contentType("application/xml")
                .body(leilao)
                .expect()
                .statusCode(200)
                .when()
                .post("leiloes")
                .andReturn()
                .xmlPath();

        Leilao resposta = path.getObject("leilao", Leilao.class);

        assertEquals("fogao", resposta.getNome());
        assertEquals("800.0", resposta.getValorInicial().toString());
        assertEquals(felipe, resposta.getUsuario());
        assertEquals(false, resposta.isUsado());


        given().contentType("application/xml")
                .body(resposta).expect().statusCode(200)
                .when()
                .delete("/leiloes/deletar")
                .andReturn()
                .asString();
    }
}
