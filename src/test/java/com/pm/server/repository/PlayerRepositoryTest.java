package com.pm.server.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import com.pm.server.TestTemplate;
import com.pm.server.datatype.Coordinate;
import com.pm.server.datatype.Player;

public class PlayerRepositoryTest extends TestTemplate {

	private PlayerRepository playerRepository;

	private Player player1;

	private Player player2;

	private static final Logger log =
			LogManager.getLogger(PlayerRepositoryTest.class.getName());

	@Before
	public void setUp() {

		playerRepository = new PlayerRepositoryImpl();

		Coordinate player1_location = new Coordinate(1.0, 0.1);
		player1 = new Player(Player.Name.Blinky);
		player1.setLocation(player1_location);

		Coordinate player2_location = new Coordinate(2.0, 0.2);
		player2 = new Player(Player.Name.Clyde);
		player2.setLocation(player2_location);

	}

	@Test
	public void unitTest_addPlayer() {

		// Given

		// When
		addPlayer_failUponException(player1);

		// Then
		Player playerFromRepository = playerRepository.getPlayerByName(player1.getName());
		assertEquals(player1, playerFromRepository);

	}

	@Test
	public void unitTest_addPlayer_multiple() {

		// Given

		// When
		addPlayer_failUponException(player1);
		addPlayer_failUponException(player2);

		// Then
		Player player_retrieved;

		player_retrieved = playerRepository.getPlayerByName(player1.getName());
		assertEquals(player1, player_retrieved);

		player_retrieved = playerRepository.getPlayerByName(player2.getName());
		assertEquals(player2, player_retrieved);

	}

	public void unitTest_addPlayer_nullPlayerLocation() throws Exception {

		// Given
		Player player = player1;
		player.setLocation(null);

		// When
		playerRepository.addPlayer(player);

		// Then
		Player playerFromRepository = playerRepository.getPlayerByName(player1.getName());
		assertEquals(player1, playerFromRepository);

	}

	@Test(expected = NullPointerException.class)
	public void unitTest_addPlayer_nullPlayer() throws Exception {

		// Given

		// When
		playerRepository.addPlayer(null);

		// Then
		// Exception thrown above

	}

	@Test(expected = IllegalArgumentException.class)
	public void unitTest_addPlayer_conflictName() throws Exception {

		// Given
		addPlayer_failUponException(player1);

		// When
		playerRepository.addPlayer(player1);

		// Then
		// Exception thrown above

	}

	@Test
	public void unitTest_deletePlayerByName() {

		// Given
		addPlayer_failUponException(player1);

		// When
		deletePlayerByName_failUponException(player1.getName());

		// Then
		assertTrue(playerRepository.getPlayerByName(player1.getName()) == null);

	}

	@Test(expected = IllegalArgumentException.class)
	public void unitTest_deletePlayerByName_noPlayer() throws Exception {

		// Given

		// When
		playerRepository.deletePlayerByName(player1.getName());

		// Then
		// Exception thrown above

	}

	@Test
	public void unitTest_getPlayerByName() {

		// Given
		addPlayer_failUponException(player1);

		// When
		Player player = playerRepository.getPlayerByName(player1.getName());

		// Then
		assertEquals(player1, player);

	}

	@Test
	public void unitTest_getPlayerByName_noPlayer() {

		// Given

		// When
		Player player = playerRepository.getPlayerByName(player1.getName());

		// Then
		assertNull(player);

	}

	@Test
	public void unitTest_getAllPlayers() {

		// Given
		addPlayer_failUponException(player1);
		addPlayer_failUponException(player2);

		// When
		List<Player> playerList = playerRepository.getAllPlayers();

		// Then
		assertEquals(2, playerList.size());
		assertTrue(playerList.contains(player1));
		assertTrue(playerList.contains(player2));

	}

	@Test
	public void unitTest_getAllPlayers_noPlayer() {

		// Given

		// When
		List<Player> playerList = playerRepository.getAllPlayers();

		// Then
		assertEquals(0, playerList.size());

	}

	@Test
	public void unitTest_setPlayerLocationByName() {

		// Given
		addPlayer_failUponException(player1);
		Coordinate oldPlayerLocation = player1.getLocation();
		Coordinate newPlayerLocation = new Coordinate(9.8, 7.6);

		// When
		playerRepository.setPlayerLocationByName(player1.getName(), newPlayerLocation);

		// Then
		Player player_newLocation = playerRepository.getPlayerByName(player1.getName());

		assertFalse(oldPlayerLocation == player_newLocation.getLocation());
		assertTrue(newPlayerLocation == player_newLocation.getLocation());

	}

	@Test
	public void unitTest_setPlayerLocationByName_sameName() {

		// Given
		addPlayer_failUponException(player1);

		// When
		playerRepository.setPlayerLocationByName(
				player1.getName(),
				player1.getLocation()
		);

		// Then
		Player updatedPlayer = playerRepository.getPlayerByName(player1.getName());

		assertEquals(updatedPlayer, player1);

	}

	@Test(expected = IllegalArgumentException.class)
	public void unitTest_setPlayerLocationByName_noPlayer() {

		// Given

		// When
		playerRepository.setPlayerLocationByName(
				player1.getName(),
				player1.getLocation()
		);

		// Then
		// Exception thrown above

	}

	@Test
	public void unitTest_setPlayerStateByName() {

		// Given
		Player player = player1;
		addPlayer_failUponException(player);
		Player.State newState = Player.State.ACTIVE;

		// When
		playerRepository.setPlayerStateByName(player1.getName(), newState);

		// Then
		Player playerResult = playerRepository.getPlayerByName(player.getName());
		assertEquals(newState, playerResult.getState());

	}

	@Test
	public void unitTest_setPlayerStateByName_sameState() {

		// Given
		Player player = player1;
		addPlayer_failUponException(player);
		Player.State state = Player.State.CAPTURED;
		playerRepository.setPlayerStateByName(player.getName(), state);

		// When
		playerRepository.setPlayerStateByName(player.getName(), state);

		// Then
		Player playerResult = playerRepository.getPlayerByName(player.getName());
		assertEquals(state, playerResult.getState());

	}

	@Test(expected = NullPointerException.class)
	public void unitTest_setPlayerStateByName_nullName() {

		// Given
		Player player = player1;
		addPlayer_failUponException(player);
		Player.State newState = Player.State.ACTIVE;

		// When
		playerRepository.setPlayerStateByName(null, newState);

		// Then
		// Exception thrown above

	}

	@Test(expected = NullPointerException.class)
	public void unitTest_setPlayerStateByName_nullState() {

		// Given
		Player player = player1;
		addPlayer_failUponException(player);

		// When
		playerRepository.setPlayerStateByName(player.getName(), null);

		// Then
		// Exception thrown above

	}

	@Test(expected = IllegalArgumentException.class)
	public void unitTest_setPlayerStateByName_powerUpState() {

		// GIven
		Player player = player1;
		addPlayer_failUponException(player);
		Player.State illegalPlayerState = Player.State.POWERUP;

		// When
		playerRepository.setPlayerStateByName(
				player.getName(), illegalPlayerState
		);

		// Then
		// Exception thrown above

	}

	@Test
	public void unitTest_numOfPlayers() {

		// Given
		assertEquals(Integer.valueOf(0), playerRepository.numOfPlayers());
		assert(playerRepository.getAllPlayers().isEmpty());

		// When
		addPlayer_failUponException(player1);

		// Then
		assertEquals(Integer.valueOf(1), playerRepository.numOfPlayers());

		// When
		deletePlayerByName_failUponException(player1.getName());

		// Then
		assertEquals(Integer.valueOf(0), playerRepository.numOfPlayers());

	}

	private void addPlayer_failUponException(Player player) {
		try {
			playerRepository.addPlayer(player);
		}
		catch(Exception e) {
			log.error(e.getMessage());
			fail();
		}
	}

	private void deletePlayerByName_failUponException(Player.Name name) {
		try {
			playerRepository.deletePlayerByName(name);
		}
		catch(Exception e) {
			log.error(e.getMessage());
			fail();
		}
	}

}
