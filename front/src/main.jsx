import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import App from './App.jsx'
import './index.css'
import { BrowserRouter } from 'react-router-dom';
import { SignupProvider } from './context/SignupContext.jsx';

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <SignupProvider>
      <App />
    </SignupProvider>
  </StrictMode>,
)
