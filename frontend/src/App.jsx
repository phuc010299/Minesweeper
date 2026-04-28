import { useState, useEffect, useCallback, useRef } from 'react';
import './index.css';

// URL trỏ sang Spring Boot API (Chạy cổng 8080)
const API_BASE = 'http://localhost:8080/api/game';

function App() {
  const [gameState, setGameState] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  
  const timerRef = useRef(null);

  const fetchGameState = useCallback(async () => {
    try {
      const response = await fetch(API_BASE);
      if (!response.ok) throw new Error("Failed to fetch game state");
      const data = await response.json();
      setGameState(data);
      setLoading(false);
    } catch (err) {
      console.error(err);
      setError("Unable to connect to game server. Khởi chạy Backend chưa?");
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchGameState();
  }, [fetchGameState]);

  useEffect(() => {
    if (gameState?.gameState === 'PLAYING') {
      timerRef.current = setInterval(() => {
        fetchGameState();
      }, 1000);
    } else {
      if (timerRef.current) clearInterval(timerRef.current);
    }
    
    return () => {
      if (timerRef.current) clearInterval(timerRef.current);
    };
  }, [gameState?.gameState, fetchGameState]);

  const handleNewGame = async (difficulty = gameState?.difficulty || 'beginner') => {
    try {
      setLoading(true);
      const res = await fetch(`${API_BASE}/new?difficulty=${difficulty.toLowerCase()}`, { method: 'POST' });
      const data = await res.json();
      setGameState(data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleCellClick = async (row, col, isRightClick = false) => {
    if (gameState?.gameState === 'WON' || gameState?.gameState === 'LOST') return;
      
    try {
      const endpoint = isRightClick ? '/flag' : '/click';
      const res = await fetch(`${API_BASE}${endpoint}?row=${row}&col=${col}`, { method: 'POST' });
      const data = await res.json();
      setGameState(data);
    } catch (err) {
      console.error(err);
    }
  };

  const formatTime = (seconds) => {
    return String(Math.min(999, Math.max(0, seconds))).padStart(3, '0');
  };

  const getSmiley = () => {
    if (gameState?.gameState === 'WON') return '😎';
    if (gameState?.gameState === 'LOST') return '😵';
    return '🙂';
  };

  if (error) {
    return (
      <div className="game-container" style={{textAlign: 'center'}}>
        <h2>⚠️ Server Connection Error</h2>
        <p>{error}</p>
        <button className="difficulty-btn active" style={{marginTop: '16px'}} onClick={fetchGameState}>
          Retry Connection
        </button>
      </div>
    );
  }

  if (loading && !gameState) return <div className="game-container">Loading Engine...</div>;
  if (!gameState) return null;

  return (
    <div className="game-container">
      {gameState.gameState === 'LOST' && <div className="game-over-overlay"></div>}
      {gameState.gameState === 'WON' && <div className="game-win-overlay"></div>}
      
      <header>
        <h1>MINESWEEPER</h1>
        
        <div className="controls-bar">
          <button 
            className={`difficulty-btn ${gameState.difficulty === 'Beginner' ? 'active' : ''}`}
            onClick={() => handleNewGame('beginner')}
          >
            Beginner
          </button>
          <button 
            className={`difficulty-btn ${gameState.difficulty === 'Intermediate' ? 'active' : ''}`}
            onClick={() => handleNewGame('intermediate')}
          >
            Intermediate
          </button>
          <button 
            className={`difficulty-btn ${gameState.difficulty === 'Expert' ? 'active' : ''}`}
            onClick={() => handleNewGame('expert')}
          >
            Expert
          </button>
        </div>

        <div className="status-bar">
          <div className="lcd-display">
            <span className="icon">🚩</span>
            {formatTime(gameState.remainingMines)}
          </div>
          
          <button 
            className="smiley-btn" 
            onClick={() => handleNewGame()}
            title="Start New Game"
          >
            {getSmiley()}
          </button>
          
          <div className="lcd-display">
            <span className="icon">⏱️</span>
            {formatTime(gameState.elapsedSeconds)}
          </div>
        </div>
      </header>

      <GameBoard 
        gameState={gameState} 
        onCellClick={handleCellClick} 
      />
      
    </div>
  );
}

const GameBoard = ({ gameState, onCellClick }) => {
  const { cells, rows, cols, hitMineRow, hitMineCol } = gameState;

  // Render Logic
  return (
    <div 
      className="board" 
      style={{ 
        gridTemplateColumns: `repeat(${cols}, 34px)`,
        gridTemplateRows: `repeat(${rows}, 34px)`
      }}
      onContextMenu={(e) => e.preventDefault()}
    >
      {cells.map((rowArr, rowIndex) => 
        rowArr.map((cell, colIndex) => {
          
          let className = "cell ";
          let content = "";
          
          const isHit = (rowIndex === hitMineRow && colIndex === hitMineCol);

          if (!cell.revealed) {
            className += "cell-hidden";
            if (cell.flagged) content = <span className="flag-icon">🚩</span>;
          } else {
            className += "cell-revealed";
            if (cell.mine) {
              if (isHit) className += " cell-mine-hit";
              content = <span className="mine-icon">💣</span>;
            } else if (cell.adjacentMines > 0) {
              className += ` num-${cell.adjacentMines}`;
              content = cell.adjacentMines;
            }
          }

          return (
            <button
              key={`${rowIndex}-${colIndex}`}
              className={className}
              onClick={(e) => { e.preventDefault(); onCellClick(rowIndex, colIndex, false); }}
              onContextMenu={(e) => { e.preventDefault(); onCellClick(rowIndex, colIndex, true); }}
              aria-label={`Cell ${rowIndex}, ${colIndex}`}
            >
              {content}
            </button>
          );
        })
      )}
    </div>
  );
};

export default App;
