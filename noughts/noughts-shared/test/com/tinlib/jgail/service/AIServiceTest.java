package com.tinlib.jgail.service;

import com.firebase.client.Firebase;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.tinlib.asynctest.AsyncTestCase;
import com.tinlib.convey.Subscriber0;
import com.tinlib.core.TinKeys;
import com.tinlib.generated.Action;
import com.tinlib.generated.Command;
import com.tinlib.generated.Game;
import com.tinlib.generated.Profile;
import com.tinlib.jgail.core.ActionScore;
import com.tinlib.jgail.core.Agent;
import com.tinlib.jgail.core.State;
import com.tinlib.services.GameOverService;
import com.tinlib.services.NextPlayerService;
import com.tinlib.test.TestConfiguration;
import com.tinlib.test.TestHelper;
import com.tinlib.test.TestUtils;
import com.tinlib.time.TimeService;
import com.tinlib.util.Procedure;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AIServiceTest extends AsyncTestCase {
  private static final String VIEWER_ID = TestUtils.newViewerId();
  private static final String VIEWER_KEY = TestUtils.newViewerKey();
  private static final String GAME_ID = TestUtils.newGameId();
  private final long ACTION = 123L;
  private final List<Command> COMMANDS =
      ImmutableList.of(Command.newBuilder().setPlayerNumber(4).build());

  @Mock
  private Agent mockAgent;
  @Mock
  private State mockState;
  @Mock
  private GameOverService mockGameOverService;
  @Mock
  private NextPlayerService mockNextPlayerService;
  @Mock
  private TimeService mockTimeService;

  private class MockAIProvider implements AIProvider {
    @Override
    public State provideState(Profile profile) {
      return mockState;
    }

    @Override
    public Agent provideAgent(Profile profile) {
      return mockAgent;
    }
  }

  private class TestActionAdapter implements AIActionAdapter {
    @Override
    public List<Command> adaptAction(long action) {
      return COMMANDS;
    }
  }

  @Test
  public void testPickComputerAction() {
    beginAsyncTestBlock();
    Game.Builder testGameBuilder = TestUtils.newGameWithTwoPlayers(VIEWER_ID, GAME_ID);
    testGameBuilder.setCurrentPlayerNumber(1);
    testGameBuilder.setProfile(1,
        testGameBuilder.getProfile(1).toBuilder().setIsComputerPlayer(true));
    testGameBuilder.setPlayer(1, VIEWER_ID);
    testGameBuilder.setIsLocalMultiplayer(true);
    final Game testGame = testGameBuilder.build();
    TestConfiguration.Builder builder = TestConfiguration.newBuilder();
    builder.setFirebase(new Firebase(TestHelper.FIREBASE_URL));
    builder.setAnonymousViewer(VIEWER_ID, VIEWER_KEY);
    builder.setCurrentGame(testGame);
    builder.setCurrentAction(TestUtils.newEmptyAction(GAME_ID).build());
    builder.bindInstance(GameOverService.class, mockGameOverService);
    builder.bindInstance(NextPlayerService.class, mockNextPlayerService);
    builder.bindInstance(TimeService.class, mockTimeService);
    builder.bindInstance(AIProvider.class, new MockAIProvider());
    builder.bindInstance(AIActionAdapter.class, new TestActionAdapter());
    TestHelper.runTest(this, builder.build(), new Procedure<TestHelper>() {
      @Override
      public void run(final TestHelper helper) {
        AIService aiService = helper.injector().get(AIService.class);
        helper.bus().once(TinKeys.AI_ACTION_SUBMITTED, new Subscriber0(){
          @Override
          public void onMessage() {
            Game expectedGame = testGame.toBuilder()
                .setLastModified(456L)
                .addSubmittedAction(TestUtils.newEmptyAction(GAME_ID)
                    .setIsSubmitted(true)
                    .setPlayerNumber(1)
                    .addCommand(Command.newBuilder().setPlayerNumber(1)))
                .setCurrentPlayerNumber(0)
                .build();
            helper.assertGameEquals(expectedGame, FINISHED_RUNNABLE);
          }
        });
        when(mockTimeService.currentTimeMillis()).thenReturn(456L);
        when(mockGameOverService.computeVictors(eq(testGame), any(Action.class)))
            .thenReturn(Optional.<List<Integer>>absent());
        when(mockNextPlayerService.nextPlayerNumber(eq(testGame), any(Action.class)))
            .thenReturn(0);
        when(mockAgent.pickActionBlocking(eq(1), eq(mockState)))
            .thenReturn(new ActionScore(ACTION, 1.0));
        helper.bus().produce(TinKeys.ACTION_SUBMITTED, testGame);
      }
    });
    endAsyncTestBlock();
  }
}
