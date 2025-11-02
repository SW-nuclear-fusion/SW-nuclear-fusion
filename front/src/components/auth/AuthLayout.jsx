import React from 'react';
import { useNavigate } from 'react-router-dom';

function AuthLayout({ title, children, onNext, nextButtonText = '다음', showBackButton = true }) {
  const navigate = useNavigate();

  const handleSubmit = (e) => {
    e.preventDefault();
    if (onNext) onNext();
  };

  return (
    <div className="bg-linear-to-t from-[#edd9ff] to-[#faf4ff] h-full flex flex-col p-6">
      <header className="flex h-[50px] items-center">
        {showBackButton && (
          <button
            onClick={() => navigate(-1)}
            className="p-0 text-4xl text-gray-600 bg-transparent border-none cursor-pointer"
          >
            &larr;
          </button>
        )}
      </header>

      <main className="flex-grow flex flex-col px-2">
        <h2 className="mt-4 mb-10 text-3xl font-semibold leading-snug">
          {title.split('\n').map((line, i) => (
            <React.Fragment key={i}>
              {line}
              <br />
            </React.Fragment>
          ))}
        </h2>

        <form onSubmit={handleSubmit} className="flex-grow flex flex-col">
          <div className="flex-grow">
            {children}
          </div>

          <button
            type="submit"
            className="w-full p-4 mt-8 text-lg font-bold text-white transition-colors rounded-xl bg-primary hover:bg-purple-700"
          >
            {nextButtonText}
          </button>
        </form>
      </main>
    </div>
  );
}

export default AuthLayout;