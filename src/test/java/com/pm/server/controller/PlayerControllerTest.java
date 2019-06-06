package com.pm.server.controller;

import com.jayway.jsonpath.JsonPath;
import com.pm.server.ControllerTestTemplate;
import com.pm.server.datatype.Coordinate;
import com.pm.server.datatype.Player;
import com.pm.server.registry.PlayerRegistry;
import com.pm.server.utils.JsonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
public class PlayerControllerTest extends ControllerTestTemplate {

	@Autowired
	private PlayerRegistry playerRegistry;

	private static final List<Coordinate> randomCoordinateList = Arrays.asList(
			new Coordinate(12345.54321, 95837.39821),
			new Coordinate(49381.30982, 39399.49932)
	);

	private static final String BASE_MAPPING = "/player";

	private static final Logger log =
			LogManager.getLogger(PlayerControllerTest.class.getName());

	@Autowired
	private WebApplicationContext webApplicationContext;

	private MockMvc mockMvc;

	@Before
	public void setUp() {
		playerRegistry.resetHard();
		mockMvc = MockMvcBuilders
				.webAppContextSetup(this.webApplicationContext)
				.build();
	}

	@After
	public void cleanUp() {
		playerRegistry.resetHard();
	}

	@Test
	public void unitTest_selectPlayer() throws Exception {

		// Given
		Player.Name playerName = Player.Name.Inky;
		final String path = pathForSelectPlayer(playerName);

		Coordinate location = randomCoordinateList.get(0);
		final String body = JsonUtils.objectToJson(location);

		// When
		mockMvc
				.perform(post(path)
						.content(body)
						.contentType(MediaType.APPLICATION_JSON)
				)

		// Then
				.andExpect(status().isOk());

	}

	@Test
	public void unitTest_selectPlayer_sameLocation() throws Exception {

		// Given
		final String pathForInky = pathForSelectPlayer(Player.Name.Inky);
		final String pathForClyde = pathForSelectPlayer(Player.Name.Clyde);

		Coordinate location = randomCoordinateList.get(0);
		final String body = JsonUtils.objectToJson(location);

		mockMvc
				.perform(post(pathForInky)
						.content(body)
						.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isOk());

		// When
		mockMvc
				.perform(post(pathForClyde)
						.content(body)
						.contentType(MediaType.APPLICATION_JSON)
				)

		// Then
				.andExpect(status().isOk());

	}

	@Test
	public void unitTest_selectPlayer_reselect() throws Exception {

		// Given
		Player.Name playerName = Player.Name.Inky;
		final String path = pathForSelectPlayer(playerName);

		Coordinate location = randomCoordinateList.get(0);
		final String body = JsonUtils.objectToJson(location);

		mockMvc
				.perform(post(path)
						.content(body)
						.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isOk());

		// When
		mockMvc
				.perform(post(path)
						.content(body)
						.contentType(MediaType.APPLICATION_JSON)
		)

		// Then
				.andExpect(status().isConflict());

	}

	@Test
	public void unitTest_selectPlayer_noLocationGiven() throws Exception {

		// Given
		Player.Name playerName = Player.Name.Inky;
		final String path = pathForSelectPlayer(playerName);

		// When
		mockMvc
				.perform(post(path)
						.contentType(MediaType.APPLICATION_JSON)
				)

		// Then
				.andExpect(status().isBadRequest());

	}

	@Test
	public void unitTest_deselectPlayer() throws Exception {

		// Given
		Player.Name player = Player.Name.Inky;
		Coordinate location = randomCoordinateList.get(0);
		selectPlayer_failUponException(player, location);

		String path = pathForDeselectPlayer(player);

		// When
		mockMvc
				.perform(delete(path))

		// Then
				.andExpect(status().isOk());

	}

	@Test
	public void unitTest_deselectPlayer_alreadyUninitialized() throws Exception {

		// Given
		Player.Name player = Player.Name.Inky;
		String path = pathForDeselectPlayer(player);

		// When
		mockMvc
				.perform(delete(path))

		// Then
				.andExpect(status().isBadRequest());

	}

	@Test
	public void unitTest_deselectPlayer_wrongName() throws Exception {

		// Given
		Player.Name player = Player.Name.Inky;
		Coordinate location = randomCoordinateList.get(0);
		selectPlayer_failUponException(player, location);

		String path = BASE_MAPPING + "/NAME_HERE";

		// When
		mockMvc
				.perform(delete(path))

		// Then
				.andExpect(status().isNotFound());

	}

	@Test
	public void unitTest_getPlayerLocation() throws Exception {

		// Given
		Player.Name player = Player.Name.Inky;
		Coordinate location = randomCoordinateList.get(0);
		selectPlayer_failUponException(player, location);

		String path = pathForGetPlayerLocation(player);

		// When
		mockMvc
				.perform(get(path))

		// Then
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.latitude")
						.value(location.getLatitude())
				)
				.andExpect(jsonPath("$.longitude")
						.value(location.getLongitude())
				);

	}

	@Test
	public void unitTest_getPlayerLocation_uninitialized() throws Exception {

		// Given
		Player.Name player = Player.Name.Inky;
		String path = pathForGetPlayerLocation(player);

		// When
		mockMvc
				.perform(get(path))

		// Then
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.latitude")
						.value(0.0)
				)
				.andExpect(jsonPath("$.longitude")
						.value(0.0)
				);

	}

	@Test
	public void unitTest_getPlayerLocation_reUninitialized() throws Exception {

		// Given
		Player.Name player = Player.Name.Inky;
		Coordinate location = randomCoordinateList.get(0);
		selectPlayer_failUponException(player, location);
		deselectPlayer_failUponException(player);

		String path = pathForGetPlayerLocation(player);

		// When
		mockMvc
				.perform(get(path))

		// Then
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.latitude")
						.value(0.0)
				)
				.andExpect(jsonPath("$.longitude")
						.value(0.0)
				);

	}

	@Test
	public void unitTest_getPlayerLocation_wrongName() throws Exception {

		// Given
		String path = BASE_MAPPING + "/PLAYER_NAME/location";

		// When
		mockMvc
				.perform(get(path))

		// Then
				.andExpect(status().isNotFound());

	}

	@Test
	public void unitTest_getAllPlayerLocations_uninitialized() throws Exception {

		// Given
		String path = pathForGetAllPlayerLocations();

		// When
		mockMvc
				.perform(get(path))

		// Then
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(5)))  // 4 Ghosts + 1 Pacman

				.andExpect(jsonPath("$[0].location.latitude").value(0.0))
				.andExpect(jsonPath("$[0].location.longitude").value(0.0))

				.andExpect(jsonPath("$[1].location.latitude").value(0.0))
				.andExpect(jsonPath("$[1].location.longitude").value(0.0))

				.andExpect(jsonPath("$[2].location.latitude").value(0.0))
				.andExpect(jsonPath("$[2].location.longitude").value(0.0))

				.andExpect(jsonPath("$[3].location.latitude").value(0.0))
				.andExpect(jsonPath("$[3].location.longitude").value(0.0))

				.andExpect(jsonPath("$[4].location.latitude").value(0.0))
				.andExpect(jsonPath("$[4].location.longitude").value(0.0));

	}

	@Test
	public void unitTest_getAllPlayerLocations_oneInitialized() throws Exception {

		// Given
		Player.Name player = Player.Name.Inky;
		Coordinate location = randomCoordinateList.get(0);
		selectPlayer_failUponException(player, location);

		String path = pathForGetAllPlayerLocations();

		// When
		mockMvc
				.perform(get(path))

		// Then
				.andExpect(status().isOk())
				.andExpect(
						jsonPath("$[?(@.name == 'Inky')].location.latitude")
						.value(location.getLatitude())
				)
				.andExpect(
						jsonPath("$[?(@.name == 'Inky')].location.longitude")
						.value(location.getLongitude())
				);

	}

	@Test
	public void unitTest_getPlayerState_uninitialized() throws Exception {

		// Given
		Player.Name player = Player.Name.Inky;
		String path = pathForGetPlayerState(player);

		// When
		mockMvc
				.perform(get(path))

		// Then
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.state")
						.value(Player.State.UNINITIALIZED.toString())
				);

	}

	@Test
	public void unitTest_getPlayerState_initialized() throws Exception {

		// Given
		Player.Name player = Player.Name.Inky;
		Coordinate location = randomCoordinateList.get(0);
		selectPlayer_failUponException(player, location);

		String path = pathForGetPlayerState(player);

		// When
		mockMvc
				.perform(get(path))

		// Then
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.state")
						.value(Player.State.READY.toString())
				);


	}

	@Test
	public void unitTest_getPlayerState_wrongName() throws Exception {

		// Given
		String path = BASE_MAPPING + "/PLAYER_NAME/state";

		// When
		mockMvc
				.perform(get(path))

		// Then
				.andExpect(status().isNotFound());

	}

	@Test
	public void unitTest_getAllPlayerStates_uninitialized() throws Exception {

		// Given
		String path = pathForGetAllPlayerStates();

		// When
		mockMvc
				.perform(get(path))

		// Then
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(5)))  // 4 Ghosts + 1 Pacman

				.andExpect(jsonPath("$[0].state")
						.value(Player.State.UNINITIALIZED.toString()))
				.andExpect(jsonPath("$[1].state")
						.value(Player.State.UNINITIALIZED.toString()))
				.andExpect(jsonPath("$[2].state")
						.value(Player.State.UNINITIALIZED.toString()))
				.andExpect(jsonPath("$[3].state")
						.value(Player.State.UNINITIALIZED.toString()))
				.andExpect(jsonPath("$[4].state")
						.value(Player.State.UNINITIALIZED.toString()));

	}

	@Test
	public void unitTest_getAllPlayerStates_oneInitialized() throws Exception {

		// Given
		Player.Name player = Player.Name.Inky;
		Coordinate location = randomCoordinateList.get(0);
		selectPlayer_failUponException(player, location);

		String path = pathForGetAllPlayerStates();

		// When
		mockMvc
				.perform(get(path))

		// Then
				.andExpect(status().isOk())
				.andExpect(
						jsonPath("$[?(@.name == 'Inky')].state")
						.value(Player.State.READY.toString())
				);

	}

	@Test
	public void unitTest_getAllPlayerDetails_uninitialized() throws Exception {

		// Given
		String path = pathForGetAllPlayerDetails();

		// When
		mockMvc
				.perform(get(path))

		// Then
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(5)))  // 4 Ghosts + 1 Pacman

				.andExpect(jsonPath("$[0].state")
						.value(Player.State.UNINITIALIZED.toString()))
				.andExpect(jsonPath("$[0].location.latitude").value(0.0))
				.andExpect(jsonPath("$[0].location.longitude").value(0.0))

				.andExpect(jsonPath("$[1].state")
						.value(Player.State.UNINITIALIZED.toString()))
				.andExpect(jsonPath("$[1].location.latitude").value(0.0))
				.andExpect(jsonPath("$[1].location.longitude").value(0.0))

				.andExpect(jsonPath("$[2].state")
						.value(Player.State.UNINITIALIZED.toString()))
				.andExpect(jsonPath("$[2].location.latitude").value(0.0))
				.andExpect(jsonPath("$[2].location.longitude").value(0.0))

				.andExpect(jsonPath("$[3].state")
						.value(Player.State.UNINITIALIZED.toString()))
				.andExpect(jsonPath("$[3].location.latitude").value(0.0))
				.andExpect(jsonPath("$[3].location.longitude").value(0.0))

				.andExpect(jsonPath("$[4].state")
						.value(Player.State.UNINITIALIZED.toString()))
				.andExpect(jsonPath("$[4].location.latitude").value(0.0))
				.andExpect(jsonPath("$[4].location.longitude").value(0.0));

	}

	@Test
	public void unitTest_getAllPlayerDetails_oneInitialized() throws Exception {

		// Given
		Player.Name player = Player.Name.Inky;
		Coordinate location = randomCoordinateList.get(0);
		selectPlayer_failUponException(player, location);

		String path = pathForGetAllPlayerDetails();

		// When
		mockMvc
				.perform(get(path))

		// Then
				.andExpect(status().isOk())
				.andExpect(
						jsonPath("$[?(@.name == 'Inky')].state")
						.value(Player.State.READY.toString())
				)
				.andExpect(
						jsonPath("$[?(@.name == 'Inky')].location.latitude")
						.value(location.getLatitude())
				)
				.andExpect(
						jsonPath("$[?(@.name == 'Inky')].location.longitude")
						.value(location.getLongitude())
				);

	}

	@Test
	public void unitTest_setPlayerLocation() throws Exception {

		// Given
		Player.Name player = Player.Name.Inky;
		Coordinate location_original = randomCoordinateList.get(0);
		selectPlayer_failUponException(player, location_original);

		String path = pathForSetPlayerLocation(player);

		Coordinate location_updated = randomCoordinateList.get(0);
		String body = JsonUtils.objectToJson(location_updated);

		// When
		mockMvc
				.perform(put(path)
						.content(body)
						.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isOk());

		// Then
		Coordinate location_result =
				getPlayerLocation_failUponException(player);
		assertEquals(location_updated.getLatitude(), location_result.getLatitude());
		assertEquals(location_updated.getLongitude(), location_result.getLongitude());

	}

	@Test
	public void unitTest_setPlayerLocation_wrongName() throws Exception {

		// Given
		String path = BASE_MAPPING + "/PLAYER_NAME/location";

		Coordinate location = randomCoordinateList.get(0);
		String body = JsonUtils.objectToJson(location);

		// When
		mockMvc
				.perform(put(path)
						.content(body)
						.contentType(MediaType.APPLICATION_JSON)
				)

		// Then
				.andExpect(status().isNotFound());

	}

	@Test
	public void unitTest_setPlayerLocation_noLocationGiven()
			throws Exception {

		// Given
		Player.Name player = Player.Name.Inky;
		Coordinate location_original = randomCoordinateList.get(0);
		selectPlayer_failUponException(player, location_original);

		String path = pathForSetPlayerLocation(player);

		// When
		mockMvc
				.perform(put(path)
						.contentType(MediaType.APPLICATION_JSON)
				)

		// Then
				.andExpect(status().isBadRequest());

	}

	@Test
	public void unitTest_setPlayerLocation_uninitialized() throws Exception {

		// Given
		Player.Name player = Player.Name.Inky;
		String path = pathForSetPlayerLocation(player);

		Coordinate location = randomCoordinateList.get(0);
		String body = JsonUtils.objectToJson(location);

		// When
		mockMvc
				.perform(put(path)
						.content(body)
						.contentType(MediaType.APPLICATION_JSON)
				)

		// Then
				.andExpect(status().isConflict());

	}

	private String pathForSelectPlayer(Player.Name player) {
		return BASE_MAPPING + "/" + player;
	}

	private String pathForDeselectPlayer(Player.Name player) {
		return BASE_MAPPING + "/" + player;
	}

	private String pathForGetPlayerLocation(Player.Name player) {
		return BASE_MAPPING + "/" + player + "/" + "location";
	}

	private String pathForGetAllPlayerLocations() {
		return BASE_MAPPING + "/" + "locations";
	}

	private String pathForGetPlayerState(Player.Name player) {
		return BASE_MAPPING + "/" + player + "/" + "state";
	}

	private String pathForGetAllPlayerStates() {
		return BASE_MAPPING + "/" + "states";
	}

	private String pathForGetAllPlayerDetails() {
		return BASE_MAPPING + "/" + "details";
	}

	private String pathForSetPlayerLocation(Player.Name player) {
		return BASE_MAPPING + "/" + player + "/" + "location";
	}

	private void selectPlayer_failUponException(
			Player.Name player, Coordinate location) {

		String path = pathForSelectPlayer(player);
		String body = JsonUtils.objectToJson(location);

		try {
			mockMvc
					.perform(post(path)
							.content(body)
							.contentType(MediaType.APPLICATION_JSON)
					)
					.andExpect(status().isOk());
		}
		catch(Exception e) {
			log.error(e.getMessage());
			fail();
		}

	}

	private void deselectPlayer_failUponException(
			Player.Name player) {

		String path = pathForSelectPlayer(player);

		try {
			mockMvc
					.perform(delete(path))
					.andExpect(status().isOk());
		}
		catch(Exception e) {
			log.error(e.getMessage());
			fail();
		}

	}

	private Coordinate getPlayerLocation_failUponException(Player.Name player) {

		String path = pathForGetPlayerLocation(player);
		String jsonContent = null;

		try {
			MvcResult result = mockMvc
					.perform(get(path))
					.andExpect(status().isOk())
					.andReturn();
			jsonContent = result.getResponse().getContentAsString();
		}
		catch(Exception e) {
			log.error(e.getMessage());
			fail();
		}

		assertNotNull(jsonContent);

		Double latitude = JsonPath.read(jsonContent, "$.latitude");
		assertNotNull(latitude);
		Double longitude = JsonPath.read(jsonContent, "$.longitude");
		assertNotNull(longitude);

		return new Coordinate(latitude, longitude);

	}

}
