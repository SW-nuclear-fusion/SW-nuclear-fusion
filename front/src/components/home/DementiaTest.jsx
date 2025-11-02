import React from "react";
import { useState } from "react";
import { GoArrowLeft } from "react-icons/go";
import { useNavigate } from "react-router-dom";
import character from "../../assets/level1purple.png";

// mock data
const content1 = [
  { id: 1, content: "1", answer: true },
  { id: 2, content: "2", answer: false },
  { id: 3, content: "3", answer: false },
  { id: 4, content: "4", answer: false },
];
const content2 = [
  { id: 1, content: "월요일", answer: true },
  { id: 2, content: "수요일", answer: false },
  { id: 3, content: "목요일", answer: false },
  { id: 4, content: "토요일", answer: false },
];
const content3 = [
  { id: 1, content: "9월", answer: true },
  { id: 2, content: "1월", answer: false },
  { id: 3, content: "3월", answer: false },
  { id: 4, content: "4월", answer: false },
];
const initialQ = (
  <div>
    어제는 수요일이었습니다
    <br />
    그렇다면 모레는 무슨 요일일까요?
  </div>
);
const changeQ = <div>오늘은 무슨 요일입니까?</div>;
const finalQ = <div>지금은 몇 월입니까?</div>;

const DementiaTest = () => {
  const navigate = useNavigate();
  const [isSelected, setIsSelected] = useState(0);
  const [question, setQuestion] = useState(initialQ);
  const [answers, setAnswers] = useState(content1);
  const [count, setCount] = useState(0);
  const [isClear, setIsClear] = useState(false);

  const handleData = (id) => {
    const selected = answers.find((a) => a.id === id);
    if (selected) {
      console.log("선택된 것:", selected.content);
    }
    setCount((prev) => prev + 1);
    setIsSelected(0);
    setQuestion(changeQ);
    setAnswers(content2);
    if (count >= 2) setIsClear(true);
  };
  return (
    <div className="bg-linear-to-t from-[#FFFFF] from-20% to-[#F5E9FF] h-full relative">
      <div className="h-[48px] border-b">top</div>
      <div className="h-[48px] flex justify-between items-center px-3">
        <GoArrowLeft
          size={24}
          onClick={() => navigate(-1)}
          className="cursor-pointer"
        />
        <div className="cursor-pointer">안내</div>
      </div>
      {/* Question */}
      <div className="text-2xl text-center py-10 font-semibold">{question}</div>
      {/* 4 Answer */}
      <div className="w-fit grid grid-cols-2 gap-3 mx-auto py-5">
        {answers.map((a) => (
          <div
            key={a.id}
            className={
              "w-[130px] h-[130px] rounded-xl flex justify-center items-center font-semibold cursor-pointer " +
              (a.id === isSelected
                ? "bg-[#F8F1FF] border-2 border-[#B259FF]"
                : "bg-white border border-[#B259FF]")
            }
            onClick={() => setIsSelected(a.id)}
          >
            {a.content}
          </div>
        ))}
      </div>
      {/* 확인 버튼 */}
      <button
        disabled={!isSelected}
        onClick={() => handleData(isSelected)}
        className={
          "absolute bottom-15 left-1/2 -translate-x-1/2 w-[330px] h-[50px] " +
          "text-white rounded-xl grid grid-cols-3 items-center cursor-pointer " +
          (isSelected ? "bg-[#B259FF] " : "bg-[#B259FF]/40")
        }
      >
        <div></div>
        <div className="text-center">확인</div>
        <div className="text-right pr-4">{count}/3</div>
      </button>
      {isClear && (
        <div className="fixed inset-0 flex items-center justify-center z-50">
          <div className="absolute inset-0 bg-black/50" />
          {/* 모달창 */}
          <div className="relative bg-white rounded-xl shadow-lg p-6 z-60 w-[340px]">
            <h2 className="text-xl font-semibold pt-6">
              퀴즈 검사를 완료했습니다.
            </h2>
            <p className="mt-2">퀴즈 결과를 확인해보세요!</p>
            <div className="flex justify-center items-center py-2">
              <img src={character} className="h-[180px]" />
            </div>
            <button
              onClick={() => navigate("/main/home")}
              className="w-full px-4 py-3 bg-[#B259FF] text-white rounded-lg"
            >
              완료
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default DementiaTest;
