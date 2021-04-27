# PremiumCore 	
<a href="https://github.com/LeonardoCod3r/PremiumCore/issues"><img alt="GitHub issues" src="https://img.shields.io/github/issues/LeonardoCod3r/PremiumCore"></a>
<a href="https://github.com/LeonardoCod3r/PremiumCore/network"><img alt="GitHub forks" src="https://img.shields.io/github/forks/LeonardoCod3r/PremiumCore"></a>
<a href="https://github.com/LeonardoCod3r/PremiumCore/stargazers"><img alt="GitHub stars" src="https://img.shields.io/github/stars/LeonardoCod3r/PremiumCore"></a>
Core para facilitar criação de plugins para servidores de Minecraft.

## Recursos

- [Hibernate 5.4.30-final](https://github.com/hibernate/hibernate-orm/tree/master/hibernate-core)
- [Hibernate-HikariCP](https://github.com/hibernate/hibernate-orm/tree/master/hibernate-hikaricp)
- [Hibernate-Ehcache](https://github.com/hibernate/hibernate-orm/tree/master/hibernate-ehcache)
- [JPA 2.2](https://github.com/hibernate/hibernate-jpa-api)
- [Google Guice](https://github.com/google/guice)
- [Google Guava](https://github.com/google/guava)
- [Annotation Command Framework](https://github.com/aikar/commands)
- ReflectionUtils by DarkBlade12
- BookViewer by DarkBlade12
- InventoryMaker
- ItemBuilder (Item.class)
- Settings (Classe para acesso à arquivos .yml)
- [EntityName](https://github.com/eduardo-mior/BukkitEnums-Translateds/blob/master/Enums/EntityName.java) (Tradutor de nomes da entidades para PT-BR)
- [ItemName](https://github.com/eduardo-mior/BukkitEnums-Translateds/blob/master/Enums/ItemName.java) (Tradutor de nomes da itens para PT-BR)

## Instalação

Para utilizar, primeiro você precisa indexar a core em seu projeto.

### Maven

1. Adicione em seu arquivo pom.xml o repositório JitPack:

```xml
<repositories>
	<repository>
		<id>jitpack.io</id>
		<url>https://jitpack.io</url>
	</repository>
</repositories>
```

2. Adicione a dependência:

```xml
<dependency>
	<groupId>com.github.LeonardoCod3r</groupId>
	<artifactId>PremiumCore</artifactId>
	<version>1.2.1</version>
	<scope>provided</scope>
</dependency>
```

### Gradle

1. Adicione em seu arquivo build.gradle o repositório JitPack:

```gradle 
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

2. Adicione a dependência:

```gradle
dependencies {
	implementation 'com.github.LeonardoCod3r:PremiumCore:1.2.1'
}
```

## Utilização
```java
public class Plugin extends JavaPlugin {

    @Getter
    private Injector injector;

    @Inject
    private PremiumCore core;
    
    @Inject
    private SessionFactory sessionFactory;

    public void onEnable() {
        this.injector = ((PremiumCore) Bukkit.getPluginManager().getPlugin("PremiumCore")).getInjector();
        injector.injectMembers(this);
    }
}
```

Para mais informações de como utilizar outros recursos, visite a nossa [Wiki](https://github.com/LeonardoCod3r/PremiumCore/wiki).

## Contribuindo
Pull requests são bem-vindas. Para mudanças importantes, abra um problema primeiro para discutir o que você gostaria de mudar.


## Licença

Licenciado por [MIT](https://choosealicense.com/licenses/mit/).
