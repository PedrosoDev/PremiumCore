# PremiumCore

Core para facilitar criação de plugins para servidores de Minecraft.

## Recursos

- [Hibernate 5.2.17-final](https://github.com/hibernate/hibernate-orm)
- [JPA 2.2](https://github.com/hibernate/hibernate-jpa-api)
- [Google Guice](https://github.com/google/guice)
- [Google Guava](https://github.com/google/guava)
- ReflectionUtils by DarkBlade12
- BookViewer by DarkBlade12
- ActionBarMessage
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
	<version>1.0.0</version>
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
	implementation 'com.github.LeonardoCod3r:PremiumCore:Tag'
}
```

## Utilização
```java
@Getter
private Injector injector;

@Inject
private PremiumCore core;

public void onEnable() {
    this.injector = Guice.createInjector(
    PluginModule.of(PremiumCore.getInstance(), PremiumCore.getInstance().getLogger())
    );
    injector.injectMembers(this);
}
```

Para mais informações de como utilizar outros recursos, visite a nossa [Wiki](https://github.com/LeonardoCod3r/PremiumCore/wiki).

## Contribuindo
Pull requests são bem-vindas. Para mudanças importantes, abra um problema primeiro para discutir o que você gostaria de mudar.


## Licença

Esse projeto é licenciado pela importante
[MIT](https://choosealicense.com/licenses/mit/).
