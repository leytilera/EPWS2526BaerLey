<#import "layout.ftlh" as layout>

    <@layout.page title="ChessFed - Play" scripts=[]>
        <div class="app-shell--nested-two-columns">
            <!-- middle column (for chessboard) -->
            <div class="center">
                <section></section>
                <section class="board-layout">
                    <!-- top player information (currently always black) -->
                    <div class="player-row">
                        <div class="board-avatar avatar--is-black" id="top-player-color" aria-hidden="true"></div>
                        <div class="player-meta">
                            <div class="player-name" id="top-player-name">Black Player</div>
                            <div class="player-status" id="top-player-status">Inactive</div>
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
                            <div class="player-status" id="bottom-player-status">Inactive</div>
                        </div>
                    </div>
                </section>
                <section></section>
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
                    <div class="move-history__footer">
                        <div id="game-finished"></div>
                    </div>
                </section>
            </aside>
        </div>
    </@layout.page>