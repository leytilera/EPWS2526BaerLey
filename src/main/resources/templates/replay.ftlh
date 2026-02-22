<#import "layout.ftlh" as layout>

    <@layout.page title="ChessFed - Replay" scripts=["/scripts/replay-game.js"]>
        <div class="app-shell--nested-two-columns">
            <!-- middle column (for chessboard) -->
            <div class="center">
                <section class="board-layout">
                    <!-- top player information (currently always black) -->
                    <div class="player-row">
                        <div class="board-avatar avatar--is-black" id="top-player-color" aria-hidden="true"></div>
                        <div class="player-meta">
                            <div class="player-name" id="top-player-name">Black player</div>
                            <div class="player-status" id="top-player-status">Finished</div>
                        </div>
                    </div>

                    <!-- chessboard -->
                    <div class="board-slot">
                        <div id="board" class="board" data-js-board=""></div>
                    </div>

                    <!-- bottom player information (currently always white) -->
                    <div class="player-row">
                        <div class="board-avatar avatar--is-white" id="bottom-player-color" aria-hidden="true"></div>
                        <div class="player-meta">
                            <div class="player-name" id="bottom-player-name">White Player</div>
                            <div class="player-status" id="bottom-player-status">Finished</div>
                        </div>
                    </div>
                </section>
            </div>

            <!-- right column (move history)-->
            <aside class="right-side-panel">

                <!-- move history -->
                <section class="move-history">
                    <header class="move-history__header">
                        <h2 class="move-history__title">Move History</h2>
                    </header>

                    <div class="move-history__body">
                        <div class="move-history__scroll">
                            <!-- rendering as table/row: MOVENUMBER | WHITE | BLACK -->
                            <div class="moves" id="move-history"></div>
                        </div>
                    </div>

                    <div class="move-history__footer move-history__footer--replay">
                        <div class="replay-text">Replay Game</div>
                        <div class="replay-buttons" data-js-replay-buttons>
                            <button class="btn" type="button" data-action="first">First</button>
                            <button class="btn" type="button" data-action="previous">◀</button>
                            <button class="btn" type="button" data-action="next">▶</button>
                            <button class="btn" type="button" data-action="last">Last</button>
                        </div>
                    </div>
                </section>
            </aside>
        </div>
    </@layout.page>