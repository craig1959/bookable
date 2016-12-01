package nz.co.skytv.bp.protocol.config;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import nz.co.skytv.bp.protocol.endpoint.lifecycle.EndpointLifecycleServiceImpl;
import nz.co.skytv.bp.protocol.model.ConfigType;
import nz.co.skytv.bp.protocol.model.EndpointType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RunWith(MockitoJUnitRunner.class)
public class ConfigServiceTest {

  private static final Logger log = LoggerFactory.getLogger(ConfigServiceTest.class);

  ConfigService configService;

  File threeEndpointsConfig;
  File twoEndpointsConfig;
  File reconfiguredEndPointsConfig;

  @Mock
  ConfigXmlFileClassLoader configLoader;
  @Mock
  EndpointLifecycleServiceImpl endpointLifecycleService;

  ConfigType configType;

  @Before
  public void init() throws Exception {
    threeEndpointsConfig = new File("src/test/resources/threeEPConfig.xml");
    twoEndpointsConfig = new File("src/test/resources/twoEPConfig.xml");
    reconfiguredEndPointsConfig = new File("src/test/resources/modifiedEPConfig.xml");
    configService = new ConfigService();
    configService.configLoader = configLoader;
    configService.endpointLifecycleService = endpointLifecycleService;
    configService.xmlFile = threeEndpointsConfig;
    configType = configTypeFixture(threeEndpointsConfig);
    when(configLoader.parseFile(any(File.class))).thenReturn(configType);
  }


  @Test
  public void shouldCallConfigLoader() throws Exception {
    configService.updateConfig();
    verify(configLoader).parseFile(any(File.class));
  }

  @Test
  public void shouldPopulateCurrentEndpoints() throws Exception {
    configService.updateConfig();
    assertThat(configService.currentEndpoints).hasSize(3);
  }

  @Test
  public void shouldCallInitiateEndpoint() throws Exception {
    configService.currentEndpoints = buildConfig(twoEndpointsConfig);
    Map<String, EndpointType> newEndpoints = buildConfig(threeEndpointsConfig);

    configService.processEndpoints(newEndpoints);
    verify(endpointLifecycleService, Mockito.times(1)).initiateEndpoint(any(EndpointType.class));
  }

  @Test
  public void shouldCallDestroyEndpoint() throws Exception {
    configService.currentEndpoints = buildConfig(threeEndpointsConfig);
    Map<String, EndpointType> newEndpoints = buildConfig(twoEndpointsConfig);

    configService.processEndpoints(newEndpoints);
    verify(endpointLifecycleService, Mockito.times(1)).destroyEndpoint(any(EndpointType.class));
  }

  @Test
  public void shouldCallReconfigureEndpoint() throws Exception {
    configService.currentEndpoints = buildConfig(threeEndpointsConfig);
    Map<String, EndpointType> newEndpoints = buildConfig(reconfiguredEndPointsConfig);

    configService.processEndpoints(newEndpoints);
    verify(endpointLifecycleService, Mockito.times(1)).reconfigureEndpoint(any(EndpointType.class));
  }


  private Map<String, EndpointType> buildConfig(File config) throws Exception {
    Map<String, EndpointType> endpoints = new HashMap<String, EndpointType>();
    configType = configTypeFixture(config);
    for (EndpointType e : configType.getEndpoint()) {
      endpoints.put(e.getId(), e);
    }
    return endpoints;
  }

  public ConfigType configTypeFixture(File config) throws Exception {
    //need jaxb to build configType as it has no constructors
    JAXBContext jaxbContext = JAXBContext.newInstance(ConfigType.class);
    Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
    JAXBElement<ConfigType> root = jaxbUnmarshaller.unmarshal(new StreamSource(config), ConfigType.class);
    return root.getValue();
  }

}
