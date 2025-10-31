import React from 'react';

/**
 * 글자크기, 성별 등에서 사용하는 큰 선택 버튼
 * @param {string} text - 버튼에 표시될 텍스트
 * @param {React.ReactNode} icon - (선택) 아이콘
 * @param {boolean} isSelected - 현재 선택되었는지 여부
 * @param {function} onClick - 클릭 이벤트
 */
function SelectionButton({ text, icon, isSelected, onClick }) {
  return (
    <button
      type="button"
      onClick={onClick}
      className={`
        w-full p-6 text-center text-lg font-medium border-2 rounded-xl transition-all
        ${
          isSelected
            ? 'bg-white border-primary shadow-lg' // 선택됨
            : 'bg-white/70 border-gray-300' // 선택 안됨
        }
      `}
    >
      {icon && <div className="text-4xl mb-2">{icon}</div>}
      {text}
    </button>
  );
}

export default SelectionButton;